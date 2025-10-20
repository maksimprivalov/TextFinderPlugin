package com.github.maksimprivalov.textfinderplugin.textfinder

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.ActionEvent
import javax.swing.*
import kotlin.io.path.Path

class TextFinderPanel : JPanel(BorderLayout()) {

    private val directoryField = JTextField("/Users/User/TextFinder/src/main")
    private val searchField = JTextField("println(")
    private val startButton = JButton("Start search")
    private val cancelButton = JButton("Cancel search")
    private val resultArea = JTextArea().apply {
        isEditable = false
    }

    private var searchJob: Job? = null // store the search coroutine job
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    // configuration of the panel UI
    init {
        val inputPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(JLabel("Directory path:"))
            add(directoryField)
            add(Box.createVerticalStrut(5))
            add(JLabel("String to search:"))
            add(searchField)
            add(Box.createVerticalStrut(10))
            add(startButton)
            add(Box.createVerticalStrut(5))
            add(cancelButton)
        }

        add(inputPanel, BorderLayout.NORTH)
        add(JScrollPane(resultArea), BorderLayout.CENTER)
        preferredSize = Dimension(600, 400)

        startButton.addActionListener(this::onStartClicked)
        cancelButton.addActionListener(this::onCancelClicked)
    }

    private fun onStartClicked(e: ActionEvent) {
        resultArea.text = ""
        startButton.isEnabled = false
        cancelButton.isEnabled = true

        val dir = directoryField.text.trim()
        val query = searchField.text.trim()
        searchJob = scope.launch(Dispatchers.IO) { // launch the search in IO dispatcher
            try {
                searchForTextOccurrences(query, Path(dir)).collect { occ ->
                    withContext(Dispatchers.Swing) { // switch to Swing dispatcher to update UI
                        resultArea.append("${occ.file.fileName}: ${occ.line}:${occ.offset}\n")
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Swing) {
                    JOptionPane.showMessageDialog(this@TextFinderPanel, "Error: ${ex.message}")
                }
            } finally {
                withContext(Dispatchers.Swing) {
                    startButton.isEnabled = true
                    cancelButton.isEnabled = false
                }
            }
        }
    }

    private fun onCancelClicked(e: ActionEvent) {
        searchJob?.cancel() // stop the search coroutine
        resultArea.append("Search canceled.\n")
        startButton.isEnabled = true
        cancelButton.isEnabled = false
    }
}

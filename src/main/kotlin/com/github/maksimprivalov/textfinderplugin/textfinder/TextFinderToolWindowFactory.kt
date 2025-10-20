package com.github.maksimprivalov.textfinderplugin.textfinder

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class TextFinderToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = TextFinderPanel()
        val content = ContentFactory.getInstance().createContent(panel, "", false)
        // Add the panel to the tool window in Content format
        toolWindow.contentManager.addContent(content)
    }
}

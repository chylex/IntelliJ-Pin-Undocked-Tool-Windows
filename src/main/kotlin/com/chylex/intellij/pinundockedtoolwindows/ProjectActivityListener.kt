package com.chylex.intellij.pinundockedtoolwindows

import com.chylex.intellij.pinundockedtoolwindows.patch.ToolWindowPatcher
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class ProjectActivityListener : ProjectActivity {
	override suspend fun execute(project: Project) {
		ApplicationManager.getApplication().invokeLater(ToolWindowPatcher::apply)
	}
}

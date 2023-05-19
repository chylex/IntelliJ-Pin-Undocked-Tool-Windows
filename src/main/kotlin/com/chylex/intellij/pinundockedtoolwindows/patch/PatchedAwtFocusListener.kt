package com.chylex.intellij.pinundockedtoolwindows.patch

import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.ToolWindowType
import com.intellij.openapi.wm.impl.ToolWindowManagerImpl
import com.intellij.toolWindow.InternalDecoratorImpl
import com.intellij.ui.ClientProperty
import java.awt.AWTEvent
import java.awt.Component
import java.awt.event.AWTEventListener
import java.awt.event.FocusEvent

@Suppress("UnstableApiUsage")
class PatchedAwtFocusListener(internal val original: AWTEventListener) : AWTEventListener {
	override fun eventDispatched(event: AWTEvent?) {
		if (event is FocusEvent && event.id == FocusEvent.FOCUS_LOST && shouldIgnoreFocusLostEvent(event.component)) {
			return
		}
		
		original.eventDispatched(event)
	}
	
	private fun shouldIgnoreFocusLostEvent(currentlyFocused: Component?): Boolean {
		if (AwtClickListener.wasPressingButton) {
			return false
		}
		
		val project = IdeFocusManager.getGlobalInstance().lastFocusedFrame?.project ?: return false
		if (project.isDisposed || project.isDefault) {
			return false
		}
		
		val toolWindowManager = ToolWindowManager.getInstance(project)
		val toolWindowId = getToolWindowIdForComponent(currentlyFocused) ?: return false
		val toolWindow = toolWindowManager.getToolWindow(toolWindowId) ?: return false
		
		return toolWindow.type == ToolWindowType.SLIDING
	}
	
	private fun getToolWindowIdForComponent(component: Component?): String? {
		var c = component
		while (c != null) {
			if (c is InternalDecoratorImpl) {
				return c.toolWindowId
			}
			c = ClientProperty.get(c, ToolWindowManagerImpl.PARENT_COMPONENT) ?: c.parent
		}
		return null
	}
}

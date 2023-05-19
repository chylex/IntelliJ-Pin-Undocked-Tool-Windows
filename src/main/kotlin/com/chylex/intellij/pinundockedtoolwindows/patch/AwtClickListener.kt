package com.chylex.intellij.pinundockedtoolwindows.patch

import com.intellij.openapi.actionSystem.AnActionHolder
import com.intellij.openapi.application.ApplicationManager
import java.awt.AWTEvent
import java.awt.event.AWTEventListener
import java.awt.event.MouseEvent
import javax.swing.AbstractButton

object AwtClickListener : AWTEventListener {
	var wasPressingButton = false
		private set
	
	override fun eventDispatched(event: AWTEvent?) {
		if (event !is MouseEvent || event.id != MouseEvent.MOUSE_RELEASED) {
			return
		}
		
		val source = event.source
		if (source is AbstractButton || source is AnActionHolder) {
			wasPressingButton = true
			ApplicationManager.getApplication().invokeLater { wasPressingButton = false }
		}
	}
}

package com.chylex.intellij.pinundockedtoolwindows.patch

import java.awt.AWTEvent
import java.awt.Toolkit
import java.awt.event.AWTEventListenerProxy

object ToolWindowPatcher {
	private const val EVENT_MASK = AWTEvent.FOCUS_EVENT_MASK or AWTEvent.WINDOW_FOCUS_EVENT_MASK
	
	private var activePatch: ActivePatch? = null
	
	private data class ActivePatch(val listener: PatchedAwtFocusListener, val eventMask: Long)
	
	fun apply() {
		if (activePatch != null) {
			return
		}
		
		val toolkit = Toolkit.getDefaultToolkit()
		for (listenerProxy in toolkit.getAWTEventListeners(EVENT_MASK)) {
			if (listenerProxy is AWTEventListenerProxy) {
				val listener = listenerProxy.listener
				if (listener.javaClass.name == "com.intellij.openapi.wm.impl.ToolWindowManagerImpl\$ToolWindowManagerAppLevelHelper\$MyListener") {
					val patch = ActivePatch(PatchedAwtFocusListener(listener), listenerProxy.eventMask)
					toolkit.removeAWTEventListener(listener)
					toolkit.addAWTEventListener(patch.listener, patch.eventMask)
					activePatch = patch
					return
				}
			}
		}
	}
	
	fun revert() {
		val patch = activePatch
		if (patch != null) {
			val toolkit = Toolkit.getDefaultToolkit()
			toolkit.removeAWTEventListener(patch.listener)
			toolkit.addAWTEventListener(patch.listener.original, patch.eventMask)
			activePatch = null
		}
	}
}

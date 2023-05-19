package com.chylex.intellij.pinundockedtoolwindows.patch

import java.awt.AWTEvent
import java.awt.Toolkit
import java.awt.event.AWTEventListenerProxy

object ToolWindowPatcher {
	private data class ActivePatch(val focusListener: PatchedAwtFocusListener, val focusEventMask: Long) {
		fun apply() = with(Toolkit.getDefaultToolkit()) {
			removeAWTEventListener(focusListener.original)
			addAWTEventListener(focusListener, focusEventMask)
			addAWTEventListener(AwtClickListener, AWTEvent.MOUSE_EVENT_MASK)
		}
		
		fun revert() = with(Toolkit.getDefaultToolkit()) {
			removeAWTEventListener(AwtClickListener)
			removeAWTEventListener(focusListener)
			addAWTEventListener(focusListener.original, focusEventMask)
		}
	}
	
	private var activePatch: ActivePatch? = null
	
	fun apply() {
		if (activePatch != null) {
			return
		}
		
		val toolkit = Toolkit.getDefaultToolkit()
		for (listenerProxy in toolkit.getAWTEventListeners(AWTEvent.FOCUS_EVENT_MASK or AWTEvent.WINDOW_FOCUS_EVENT_MASK)) {
			if (listenerProxy is AWTEventListenerProxy) {
				val listener = listenerProxy.listener
				if (listener.javaClass.name == "com.intellij.openapi.wm.impl.ToolWindowManagerImpl\$ToolWindowManagerAppLevelHelper\$MyListener") {
					val patch = ActivePatch(PatchedAwtFocusListener(listener), listenerProxy.eventMask)
					patch.apply()
					activePatch = patch
					return
				}
			}
		}
	}
	
	fun revert() {
		val patch = activePatch
		if (patch != null) {
			patch.revert()
			activePatch = null
		}
	}
}

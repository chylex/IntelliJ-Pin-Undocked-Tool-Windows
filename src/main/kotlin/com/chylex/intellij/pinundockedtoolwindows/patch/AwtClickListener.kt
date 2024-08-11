package com.chylex.intellij.pinundockedtoolwindows.patch

import com.intellij.openapi.actionSystem.AnActionHolder
import java.awt.AWTEvent
import java.awt.event.AWTEventListener
import java.awt.event.MouseEvent
import java.util.Timer
import java.util.concurrent.atomic.AtomicInteger
import javax.swing.AbstractButton
import kotlin.concurrent.schedule

object AwtClickListener : AWTEventListener {
	private val timer = Timer("Pin Undocked Tool Windows Click Timer", true)
	private val pressingButtonCounter = AtomicInteger(0)
	
	val wasPressingButton
		get() = pressingButtonCounter.get() > 0
	
	override fun eventDispatched(event: AWTEvent?) {
		if (event !is MouseEvent || event.id != MouseEvent.MOUSE_RELEASED) {
			return
		}
		
		val source = event.source
		if (source is AbstractButton || source is AnActionHolder) {
			pressingButtonCounter.incrementAndGet()
			timer.schedule(200L) { pressingButtonCounter.decrementAndGet() }
		}
	}
}

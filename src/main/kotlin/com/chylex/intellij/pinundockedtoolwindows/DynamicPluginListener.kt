package com.chylex.intellij.pinundockedtoolwindows

import com.chylex.intellij.pinundockedtoolwindows.patch.ToolWindowPatcher
import com.intellij.ide.plugins.DynamicPluginListener
import com.intellij.ide.plugins.IdeaPluginDescriptor

const val PLUGIN_ID = "com.chylex.intellij.pinundockedtoolwindows"

class DynamicPluginListener : DynamicPluginListener {
	override fun pluginLoaded(pluginDescriptor: IdeaPluginDescriptor) {
		if (pluginDescriptor.pluginId.idString == PLUGIN_ID) {
			ToolWindowPatcher.apply()
		}
	}
	
	override fun beforePluginUnload(pluginDescriptor: IdeaPluginDescriptor, isUpdate: Boolean) {
		if (pluginDescriptor.pluginId.idString == PLUGIN_ID) {
			ToolWindowPatcher.revert()
		}
	}
}

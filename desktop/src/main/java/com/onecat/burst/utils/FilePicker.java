package com.onecat.burst.utils;

import com.badlogic.gdx.utils.Null;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.nfd.NFDFilterItem;
import org.lwjgl.util.nfd.NativeFileDialog;
import java.nio.ByteBuffer;

public class FilePicker {

	enum Mode {
		OPEN,
		SAVE
	}

	public static @Null String pick(Mode mode) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			PointerBuffer outPath = stack.mallocPointer(1);
			NFDFilterItem filterItem = NFDFilterItem.malloc(stack)
					.name(stack.UTF8("Particle Effects (*.pfx)"))
					.spec(stack.UTF8("*.pfx;*.PFX"));
			NFDFilterItem.Buffer filterList = NFDFilterItem.malloc(1, stack);
			filterList.put(0, filterItem);
			String defaultPath = System.getProperty("user.home");
			int result = NativeFileDialog.NFD_OKAY;
			if (mode == Mode.OPEN)
				result = NativeFileDialog.NFD_OpenDialog(outPath, filterList, stack.UTF8(defaultPath));
			else if (mode == Mode.SAVE)
				result = NativeFileDialog.NFD_SaveDialog(outPath, filterList, stack.UTF8(defaultPath), stack.UTF8("new_project.pfx"));

			if (result == NativeFileDialog.NFD_OKAY) {
				long pathAddress = outPath.get(0);
				ByteBuffer pathBuffer = MemoryUtil.memByteBufferNT1(pathAddress);
				String selectedPath = MemoryUtil.memUTF8(pathBuffer);
				NativeFileDialog.NFD_FreePath(pathAddress);
				return selectedPath;
			}
			else if (result == NativeFileDialog.NFD_CANCEL) {
				Log.l("File selection cancelled");
				return null;
			}
			else {
				Log.e("NFD error: " + NativeFileDialog.NFD_GetError());
				return null;
			}
		}
	}

}

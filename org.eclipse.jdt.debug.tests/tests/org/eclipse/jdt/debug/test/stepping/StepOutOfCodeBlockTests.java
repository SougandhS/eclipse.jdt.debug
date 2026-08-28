/*******************************************************************************
 *  Copyright (c) 2026 IBM Corporation.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.debug.test.stepping;

import org.eclipse.core.commands.Command;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.testplugin.DebugElementKindEventWaiter;
import org.eclipse.jdt.debug.testplugin.DebugEventWaiter;
import org.eclipse.jdt.debug.tests.AbstractDebugTest;
import org.eclipse.jdt.debug.tests.TestUtil;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

public class StepOutOfCodeBlockTests extends AbstractDebugTest {

	private static final String STEP_OUT_OF_CODE_BLOCK_COMMAND = "org.eclipse.jdt.debug.ui.StepOutOfCodeBlock";
	private static final String TYPE_NAME = "StepOutCodeBlockPgm";
	private boolean fSavedSkipBreakpoints;

	public StepOutOfCodeBlockTests(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		fSavedSkipBreakpoints = DebugUITools.getPreferenceStore().getBoolean(IDebugUIConstants.PREF_SKIP_BREAKPOINTS_DURING_RUN_TO_LINE);
		DebugUITools.getPreferenceStore().setValue(IDebugUIConstants.PREF_SKIP_BREAKPOINTS_DURING_RUN_TO_LINE, true);
	}

	@Override
	protected void tearDown() throws Exception {
		DebugUITools.getPreferenceStore().setValue(IDebugUIConstants.PREF_SKIP_BREAKPOINTS_DURING_RUN_TO_LINE, fSavedSkipBreakpoints);
		super.tearDown();
	}

	public void testStepOutOfWhileBlock() throws Exception {
		ILineBreakpoint bp = createLineBreakpoint(27, TYPE_NAME);
		IJavaThread thread = null;
		try {
			thread = launchToLineBreakpoint(TYPE_NAME, bp);
			JDIThread jThread = (JDIThread) thread;
			assertSuspendedAt(jThread, 27, "Breakpoint not hit inside while body");
			runAndWaitForSuspendEvent(this::stepOutOfTheCodeBlock);
			assertSuspendedAt(jThread, 31, "Wrong line after step-out of while block");
		} finally {
			cleanUp(bp, thread);
		}
	}

	public void testStepOutOfIfBlock() throws Exception {
		ILineBreakpoint bp = createLineBreakpoint(33, TYPE_NAME);
		IJavaThread thread = null;
		try {
			thread = launchToLineBreakpoint(TYPE_NAME, bp);
			JDIThread jThread = (JDIThread) thread;
			assertSuspendedAt(jThread, 33, "Breakpoint not hit inside if body");
			runAndWaitForSuspendEvent(this::stepOutOfTheCodeBlock);
			assertSuspendedAt(jThread, 41, "Wrong line after step-out of if block");
		} finally {
			cleanUp(bp, thread);
		}
	}

	public void testStepOutOfNestedIfBlock() throws Exception {
		ILineBreakpoint bp = createLineBreakpoint(35, TYPE_NAME);
		IJavaThread thread = null;
		try {
			thread = launchToLineBreakpoint(TYPE_NAME, bp);
			JDIThread jThread = (JDIThread) thread;
			assertSuspendedAt(jThread, 35, "Breakpoint not hit inside nested if body");
			runAndWaitForSuspendEvent(this::stepOutOfTheCodeBlock);
			assertSuspendedAt(jThread, 38, "Wrong line after step-out of nested if block");
		} finally {
			cleanUp(bp, thread);
		}
	}

	public void testStepOutOfAnonymousBlock() throws Exception {
		ILineBreakpoint bp = createLineBreakpoint(43, TYPE_NAME);
		IJavaThread thread = null;
		try {
			thread = launchToLineBreakpoint(TYPE_NAME, bp);
			JDIThread jThread = (JDIThread) thread;
			assertSuspendedAt(jThread, 43, "Breakpoint not hit inside anonymous block");
			runAndWaitForSuspendEvent(this::stepOutOfTheCodeBlock);
			assertSuspendedAt(jThread, 46, "Wrong line after step-out of anonymous block");
		} finally {
			cleanUp(bp, thread);
		}
	}

	public void testStepOutOfForEachBlock() throws Exception {
		ILineBreakpoint bp = createLineBreakpoint(48, TYPE_NAME);
		IJavaThread thread = null;
		try {
			thread = launchToLineBreakpoint(TYPE_NAME, bp);
			JDIThread jThread = (JDIThread) thread;
			assertSuspendedAt(jThread, 48, "Breakpoint not hit inside for-each body");
			runAndWaitForSuspendEvent(() -> jThread.stepOver());
			runAndWaitForSuspendEvent(this::stepOutOfTheCodeBlock);
			assertSuspendedAt(jThread, 51, "Wrong line after step-out of for-each block");
		} finally {
			cleanUp(bp, thread);
		}
	}

	public void testStepOutOfDoWhileBlock() throws Exception {
		ILineBreakpoint bp = createLineBreakpoint(53, TYPE_NAME);
		IJavaThread thread = null;
		try {
			thread = launchToLineBreakpoint(TYPE_NAME, bp);
			JDIThread jThread = (JDIThread) thread;
			assertSuspendedAt(jThread, 53, "Breakpoint not hit inside do-while body");
			runAndWaitForSuspendEvent(this::stepOutOfTheCodeBlock);
			assertSuspendedAt(jThread, 56, "Wrong line after step-out of do-while block");
		} finally {
			cleanUp(bp, thread);
		}
	}

	public void testStepOutOfTryBlock() throws Exception {
		ILineBreakpoint bp = createLineBreakpoint(58, TYPE_NAME);
		IJavaThread thread = null;
		try {
			thread = launchToLineBreakpoint(TYPE_NAME, bp);
			JDIThread jThread = (JDIThread) thread;
			assertSuspendedAt(jThread, 58, "Breakpoint not hit inside try body");
			runAndWaitForSuspendEvent(() -> jThread.stepOver());
			runAndWaitForSuspendEvent(this::stepOutOfTheCodeBlock);
			assertSuspendedAt(jThread, 67, "Wrong line after step-out of try block");
		} finally {
			cleanUp(bp, thread);
		}
	}

	public void testStepOutOfCatchBlock() throws Exception {
		ILineBreakpoint bp = createLineBreakpoint(61, TYPE_NAME);
		IJavaThread thread = null;
		try {
			thread = launchToLineBreakpoint(TYPE_NAME, bp);
			JDIThread jThread = (JDIThread) thread;
			assertSuspendedAt(jThread, 61, "Breakpoint not hit inside catch body");
			runAndWaitForSuspendEvent(() -> jThread.stepOver());
			runAndWaitForSuspendEvent(this::stepOutOfTheCodeBlock);
			assertSuspendedAt(jThread, 67, "Wrong line after step-out of catch block");
		} finally {
			cleanUp(bp, thread);
		}
	}

	public void testStepOutOfTrailingAnonymousBlock() throws Exception {
		ILineBreakpoint bp = createLineBreakpoint(69, TYPE_NAME);
		IJavaThread thread = null;
		try {
			thread = launchToLineBreakpoint(TYPE_NAME, bp);
			JDIThread jThread = (JDIThread) thread;
			assertSuspendedAt(jThread, 69, "Breakpoint not hit inside trailing anonymous block");
			runAndWaitForSuspendEvent(() -> jThread.stepOver());
			runAndWaitForSuspendEvent(this::stepOutOfTheCodeBlock);
			assertSuspendedAt(jThread, 18, "Wrong line after step-out of trailing anonymous block");
		} finally {
			cleanUp(bp, thread);
		}
	}

	private void assertSuspendedAt(JDIThread jThread, int expectedLine, String message) throws Exception {
		assertTrue("Thread did not suspend", jThread.isSuspended());
		IJavaStackFrame frame = (IJavaStackFrame) jThread.getTopStackFrame();
		assertEquals(message, expectedLine, frame.getLineNumber());
	}

	private void runAndWaitForSuspendEvent(ISafeRunnable runnable) throws Exception {
		DebugEventWaiter waiter = new DebugElementKindEventWaiter(DebugEvent.SUSPEND, IJavaThread.class);
		runnable.run();
		Object event = waiter.waitForEvent();
		assertNotNull("Timed out waiting for SUSPEND event after " + runnable, event);
		TestUtil.waitForJobs(getName(), 100, DEFAULT_TIMEOUT);
	}

	private void stepOutOfTheCodeBlock() throws Exception {
		waitUntilCommandEnabled(STEP_OUT_OF_CODE_BLOCK_COMMAND);
		DebugUIPlugin.getStandardDisplay().syncExec(() -> {
			IHandlerService hs = PlatformUI.getWorkbench().getService(IHandlerService.class);
			try {
				hs.executeCommand(STEP_OUT_OF_CODE_BLOCK_COMMAND, null);
			} catch (Exception e) {
				DebugPlugin.log(e);
				fail("StepOutOfCodeBlock command execution failed: " + e);
			}
		});
	}

	private void waitUntilCommandEnabled(String commandId) throws Exception {
		ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
		Command command = commandService.getCommand(commandId);
		long end = System.currentTimeMillis() + DEFAULT_TIMEOUT;
		boolean[] enabled = new boolean[1];
		do {
			TestUtil.waitForJobs(getName(), 50, 200);
			DebugUIPlugin.getStandardDisplay().syncExec(() -> enabled[0] = command.isEnabled());
			if (enabled[0]) {
				return;
			}
			Thread.sleep(50);
		} while (System.currentTimeMillis() < end);
		fail("Command " + commandId + " never became enabled within " + DEFAULT_TIMEOUT + "ms");
	}

	private void cleanUp(ILineBreakpoint bp, IJavaThread thread) throws CoreException {
		bp.delete();
		terminateAndRemove(thread);
		removeAllBreakpoints();
	}

	@Override
	protected boolean enableUIEventLoopProcessingInWaiter() {
		return false;
	}
}

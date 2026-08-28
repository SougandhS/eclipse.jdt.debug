/*******************************************************************************
 * Copyright (c) 2026 IBM Corporation.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.debug.ui.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.ui.actions.IRunToLineTarget;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.internal.debug.ui.EvaluationContextManager;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Handler for the "Step Out of Code Block" debug action.
 */
public class StepOutOfCodeBlockHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if (editor != null) {
			ISelection selection = editor.getEditorSite().getSelectionProvider().getSelection();
			IJavaStackFrame frame = EvaluationContextManager.getEvaluationContext(editor);
			if (selection instanceof ITextSelection selection2) {
				try {
					IRunToLineTarget runToLineAction = new RunToLineAdapter(frame.getLineNumber(), true);
					runToLineAction.runToLine(editor, selection2, frame.getThread());
				} catch (CoreException e) {
					DebugPlugin.log(e);
				}
			}
		}
		return null;
	}
}

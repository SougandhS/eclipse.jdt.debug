/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.debug.ui.actions;


import java.text.MessageFormat;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.internal.debug.ui.launcher.RuntimeClasspathViewer;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.dialogs.StatusDialog;
import org.eclipse.jdt.internal.ui.wizards.IStatusChangeListener;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.SourceAttachmentBlock;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.help.WorkbenchHelp;

/**
 * Attach source to an archive or variable.
 */
public class AttachSourceAction extends RuntimeClasspathAction {
	
	private IRuntimeClasspathEntry fEntry;

	// a dialog to set the source attachment properties
	private class SourceAttachmentDialog extends StatusDialog implements IStatusChangeListener {
		
		private SourceAttachmentBlock fSourceAttachmentBlock;
				
		public SourceAttachmentDialog(Shell parent, IRuntimeClasspathEntry entry) {
			super(parent);
			setTitle(MessageFormat.format(ActionMessages.getString("AttachSourceAction.Attachments_For_____{0}_____1"),new String[] {entry.getPath().toString()})); //$NON-NLS-1$
			fSourceAttachmentBlock= new SourceAttachmentBlock(this, entry.getClasspathEntry(), null, null);
		}
		
		/*
		 * @see Windows#configureShell
		 */
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			WorkbenchHelp.setHelp(newShell, IJavaHelpContextIds.SOURCE_ATTACHMENT_DIALOG);
		}		
				
		protected Control createDialogArea(Composite parent) {
			Composite composite= (Composite)super.createDialogArea(parent);
						
			Control inner= fSourceAttachmentBlock.createControl(composite);
			inner.setLayoutData(new GridData(GridData.FILL_BOTH));
			return composite;
		}
		
		public void statusChanged(IStatus status) {
			updateStatus(status);
		}
		
		public IPath getSourceAttachmentPath() {
			return fSourceAttachmentBlock.getSourceAttachmentPath();
		}
		
		public IPath getSourceAttachmentRootPath() {
			return fSourceAttachmentBlock.getSourceAttachmentRootPath();
		}
				
	}
	
	/**
	 * Creates an action to open a source attachment dialog.
	 * 
	 * @param viewer the viewer the action is assocaited with or <code>null</code>
	 * @param style a button or radio button
	 */
	public AttachSourceAction(RuntimeClasspathViewer viewer, int style) {
		super((style == SWT.RADIO) ? ActionMessages.getString("AttachSourceAction.2") : ActionMessages.getString("AttachSourceAction.3"), viewer); //$NON-NLS-1$ //$NON-NLS-2$
	}	

	/**
	 * Prompts source attachment.
	 * 
	 * @see IAction#run()
	 */	
	public void run() {
		SourceAttachmentDialog dialog = new SourceAttachmentDialog(getShell(), fEntry);
		int res = dialog.open();
		if (res == Window.OK) {
			fEntry.setSourceAttachmentPath(dialog.getSourceAttachmentPath());
			fEntry.setSourceAttachmentRootPath(dialog.getSourceAttachmentRootPath());
			getViewer().refresh(fEntry);
			getViewer().notifyChanged();
		}
	}

	/**
	 * @see SelectionListenerAction#updateSelection(IStructuredSelection)
	 */
	protected boolean updateSelection(IStructuredSelection selection) {
		if (getViewer().isEnabled() && selection.size() == 1) {
			fEntry = (IRuntimeClasspathEntry)selection.getFirstElement();
			int type = fEntry.getType();
			return type == IRuntimeClasspathEntry.VARIABLE || type == IRuntimeClasspathEntry.ARCHIVE;
		} else {
			return false;
		} 
	}
	
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.web.admin.users;

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class PasswordDialog extends AbstractFormDialog<String> {
	private static final long serialVersionUID = 1L;
	private DialogButton ok;
	private DialogButton cancel;
	protected final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	private final Form<String> form = new Form<>("form");
	private final PasswordTextField pass = new PasswordTextField("password");

	public PasswordDialog(String id) {
		super(id, "");
	}

	public UserForm getUserForm() {
		return findParent(UserForm.class);
	}

	@Override
	protected void onInitialize() {
		setTitle(Model.of(getString("537")));
		ok = new DialogButton("ok", getString("54"));
		cancel = new DialogButton("cancel", getString("lbl.cancel"));
		add(form.add(feedback, pass.setRequired(false).setLabel(Model.of(getString("110")))));
		super.onInitialize();
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(ok, cancel);
	}

	@Override
	public DialogButton getSubmitButton() {
		return ok;
	}

	@Override
	public Form<?> getForm() {
		return form;
	}

	@Override
	protected void onError(AjaxRequestTarget target) {
		target.add(feedback);
	}

	@Override
	public void onClick(AjaxRequestTarget target, DialogButton button) {
		if (!form.hasError() || !button.equals(ok)) {
			super.onClick(target, button);
		}
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		final UserForm uf = getUserForm();
		if (uf.isAdminPassRequired()) {
			final UserDao dao = getBean(UserDao.class);
			if (dao.verifyPassword(getUserId(), pass.getConvertedInput())) {
				uf.saveUser(target);
			} else {
				form.error(getString("error.bad.password"));
				target.add(feedback);
			}
		}
	}
}

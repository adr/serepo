package ch.hsr.isf.serepo.client.webapp.window;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class PromptWindow extends Window {

	private static final long serialVersionUID = -1019307167205927673L;

	public enum Mode { OK, YES_NO };
	public enum Answer { OK, YES, NO };
	
	private static final String BUTTON_WIDTH = "90px";
	
	public interface AnswerCall {
		void answer(Answer answer);
	}
	
	public PromptWindow(final String prompt, Mode mode, final AnswerCall answerCall) {
	  this (null, prompt, mode, answerCall);
	}
	
	public PromptWindow(final String caption, final String prompt, Mode mode, final AnswerCall answerCall) {

		setCaption(caption);
		setSizeUndefined();
		
		VerticalLayout vl = new VerticalLayout();
		vl.setSizeUndefined();
		vl.setSpacing(true);
		vl.setMargin(true);
		center();
		setModal(true);
		setResizable(false);
		setClosable(false);
		
		Label lblPrompt = new Label(prompt);
		vl.addComponent(lblPrompt);
		vl.setExpandRatio(lblPrompt, 1.0f);
		
		switch (mode) {
		case OK:
			Button btnOK = new Button("OK", FontAwesome.CHECK);
			btnOK.addClickListener(new ClickListener() {
				private static final long serialVersionUID = -8670646099614061434L;
				@Override
				public void buttonClick(ClickEvent event) {
					if (answerCall != null) {
						answerCall.answer(Answer.OK);
					}
					close();
				}
			});
			btnOK.setWidth(BUTTON_WIDTH);
			btnOK.addStyleName(ValoTheme.BUTTON_PRIMARY);
			vl.addComponent(btnOK);
			vl.setComponentAlignment(btnOK, Alignment.MIDDLE_CENTER);
			break;
		case YES_NO:
			Button btnYes = new Button("Yes", FontAwesome.CHECK);
			btnYes.addClickListener(new ClickListener() {
				private static final long serialVersionUID = -6372064156790468993L;
				@Override
				public void buttonClick(ClickEvent event) {
					if (answerCall != null) {
						answerCall.answer(Answer.YES);
					}
					close();
				}
			});
			btnYes.setWidth(BUTTON_WIDTH);
			btnYes.addStyleName(ValoTheme.BUTTON_FRIENDLY);
			
			Button btnNo = new Button("No", FontAwesome.TIMES);
			btnNo.setWidth(BUTTON_WIDTH);
			btnNo.addClickListener(new ClickListener() {
				private static final long serialVersionUID = 1721423439683140110L;
				@Override
				public void buttonClick(ClickEvent event) {
					if (answerCall != null) {
						answerCall.answer(Answer.NO);
					}
					close();
				}
			});
			btnNo.addStyleName(ValoTheme.BUTTON_DANGER);

			HorizontalLayout hl = new HorizontalLayout(btnYes, btnNo);
			hl.setSpacing(true);
			hl.setSizeUndefined();
			vl.addComponent(hl);
			vl.setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
			break;
		}
		
		setContent(vl);
		UI.getCurrent().addWindow(this);
		
	}

}

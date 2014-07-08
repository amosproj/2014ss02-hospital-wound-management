
package com.fau.amos.team2.WoundManagement.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;

import com.fau.amos.team2.WoundManagement.model.Employee;
import com.fau.amos.team2.WoundManagement.model.Wound;
import com.fau.amos.team2.WoundManagement.model.WoundDescription;
import com.fau.amos.team2.WoundManagement.model.WoundLevel;
import com.fau.amos.team2.WoundManagement.model.WoundLevelState;
import com.fau.amos.team2.WoundManagement.model.WoundType;
import com.fau.amos.team2.WoundManagement.provider.WoundDescriptionProvider;
import com.fau.amos.team2.WoundManagement.provider.WoundLevelProvider;
import com.fau.amos.team2.WoundManagement.provider.WoundTypeProvider;
import com.fau.amos.team2.WoundManagement.resources.MessageResources;
import com.fau.amos.team2.WoundManagement.ui.subviews.BackButton;
import com.fau.amos.team2.WoundManagement.ui.subviews.UserBar;
import com.vaadin.addon.responsive.Responsive;
import com.vaadin.addon.touchkit.ui.DatePicker;
import com.vaadin.addon.touchkit.ui.NumberField;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;

@Theme("wm-responsive")
@PreserveOnRefresh
@SuppressWarnings("serial")
public class CreateWoundDescriptionView extends SessionedNavigationView {

	private Wound wound;
	private WoundDescriptionProvider woundDescriptionProvider = WoundDescriptionProvider
			.getInstance();
	private WoundLevelProvider woundLevelProvider = WoundLevelProvider
			.getInstance();
	private WoundTypeProvider woundTypeProvider = WoundTypeProvider
			.getInstance();
	private Upload upload;

	public CreateWoundDescriptionView() {

		this.wound = getEnvironment().getCurrentWound();
		WoundDescription latest = woundDescriptionProvider
				.getNewestForWound(wound);

		Employee user = getEnvironment().getCurrentEmployee();
		setCaption(MessageResources.getString("newDesc"));

		final FormLayout mainLayout = new FormLayout();
		mainLayout.setWidth("100%");
		setRightComponent(new UserBar(this));
		mainLayout.setSizeUndefined();

		CssLayout greetingdate = new CssLayout();
		greetingdate.addStyleName("greetingdate");
		greetingdate.setWidth("100%");

		new Responsive(greetingdate);

		Label erstellerLabel = new Label();
		erstellerLabel.setValue(MessageResources.getString("author") + ": "
				+ user.getFirstName() + " " + user.getLastName());
		erstellerLabel.addStyleName("erstellerLabel");
		greetingdate.addComponent(erstellerLabel);

		new Responsive(greetingdate);

		// DateField - when is the wound recorded/as seen in NewWoundView
		final DatePicker recorded = new DatePicker(
				MessageResources.getString("createDate") + ":");
		recorded.setValue(new Date());
		recorded.setLocale(getLocale());
		recorded.setInvalidAllowed(false);
		recorded.addStyleName("recorded");
		recorded.setImmediate(true);
		
		new Responsive(recorded);

		greetingdate.addComponent(recorded);
		mainLayout.addComponent(greetingdate);

		final CheckBox taschen = new CheckBox();
		final TextField bagLocation = new TextField();
		final TextField bagDirection = new TextField();

		CssLayout taschenErfassen = new CssLayout();

		taschen.setCaption(MessageResources.getString("woundBags") + ":");
		taschen.setImmediate(true);
		taschen.addStyleName("taschen");
		taschen.setValue(latest.isBaggy());

		bagLocation.setCaption(MessageResources.getString("baglocation") + ":");
		bagLocation.setImmediate(true);
		bagLocation.addStyleName("bagLocation");
		bagLocation.setMaxLength(200);
		if (latest.getBagLocation() != null) {
			bagLocation.setValue(latest.getBagLocation());
		}

		bagDirection.setCaption(MessageResources.getString("bagdirection")
				+ ":");
		bagDirection.setImmediate(true);
		bagDirection.addStyleName("bagDirection");
		bagDirection.setMaxLength(200);
		if (latest.getBagDirection() != null) {
			bagDirection.setValue(latest.getBagDirection());
		}

		taschenErfassen.addComponents(taschen, bagLocation, bagDirection);

		mainLayout.addComponent(taschenErfassen);

		// TextField - commentary
		final TextArea comment = new TextArea();
		comment.setInputPrompt(MessageResources.getString("description") + ":");
		comment.setMaxLength(2000);
		comment.setWidth("61em");
		comment.setHeight("5em");
		if (latest.getDescription() != null) {
			comment.setValue(latest.getDescription());
		}
		comment.setImmediate(true);

		mainLayout.addComponent(comment);

		// NumberField - length of wound
		final NumberField size1 = new NumberField(
				MessageResources.getString("height") + ":");
		// NumberField - width of wound
		final NumberField size2 = new NumberField(
				MessageResources.getString("width") + ":");
		// NumberField - depth of wound
		final NumberField depth = new NumberField(
				MessageResources.getString("depth") + ":");

		CssLayout wundGroessen = new CssLayout();

		// size1.setValue("0");
		size1.setInvalidAllowed(false);
		size1.addStyleName("size1");
		size1.setImmediate(true);
		
		wundGroessen.addComponent(size1);
		if (latest.getSize1() != 0) {
			size1.setValue(latest.getSize1() + "");
		}

		// size2.setValue("0");
		size2.setInvalidAllowed(false);
		size2.addStyleName("size2");
		size2.setImmediate(true);
		
		wundGroessen.addComponent(size2);
		if (latest.getSize2() != 0) {
			size2.setValue(latest.getSize2() + "");
		}

		// depth.setValue("0");
		depth.setInvalidAllowed(false);
		depth.addStyleName("depth");
		depth.setImmediate(true);
		
		wundGroessen.addComponent(depth);
		if (latest.getDepth() != 0) {
			depth.setValue(latest.getDepth() + "");
		}

		mainLayout.addComponent(wundGroessen);

		/*
		 * Woundtype & Level comboboxes
		 */
		// ComboBox - wound type
		Collection<Object> typeIds = WoundTypeProvider.getInstance().getAll()
				.getItemIds();
		final NativeSelect type = new NativeSelect(
				MessageResources.getString("woundType") + ":");
		for (Object o : typeIds) {
			WoundType tmp = WoundTypeProvider.getInstance().getByID(o);
			type.addItem(o);
			type.setItemCaption(o, tmp.getClassification());
		}
		type.setWidth("20em");
		type.setNewItemsAllowed(false);
		type.setImmediate(true);
		// type.setTextInputAllowed(false);
		if (latest.getWoundType() != null) {
			type.setValue(latest.getWoundType().getId());
		}

		// ComboBox - woundlevel
		Collection<Object> levelIds = WoundLevelProvider.getInstance().getAll()
				.getItemIds();
		final NativeSelect level = new NativeSelect(
				MessageResources.getString("woundLevel") + ":");
		for (Object o : levelIds) {
			WoundLevel tmp = WoundLevelProvider.getInstance().getByID(o);
			level.addItem(o);
			level.setItemCaption(o, tmp.getCharacterisation());
		}
		level.setNewItemsAllowed(false);
		level.setImmediate(true);
		level.setWidth("20em");
		if (latest.getWoundLevel() != null) {
			level.setValue(latest.getWoundLevel().getId());
		}

		CssLayout woundlevelandtype = new CssLayout();

		type.addStyleName("type");
		woundlevelandtype.addComponent(type);

		level.addStyleName("level");
		woundlevelandtype.addComponent(level);
		mainLayout.addComponent(woundlevelandtype);

		final WoundDescription woundDescription = new WoundDescription();
		woundDescription.setEmployee(user);
		woundDescription.setWound(wound);

		Button createNewDescription = new Button(
				MessageResources.getString("createDesc"));
		createNewDescription.setStyleName("btn-default");
		createNewDescription.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {

				woundDescription.setBaggy(taschen.getValue());

				if (bagLocation.getValue() != null) {
					woundDescription.setBagLocation(bagLocation.getValue());
				}

				if (bagDirection.getValue() != null) {
					woundDescription.setBagDirection(bagDirection.getValue());
				}

				// setWoundLevel
				if (level.getValue() != null) {
					woundDescription.setWoundLevel(woundLevelProvider
							.getByID(level.getValue()));
				}

				// setWoundType
				if (type.getValue() != null) {
					WoundType woundType = woundTypeProvider.getByID(type
							.getValue());
					woundDescription.setWoundType(woundType);

					// check if WoundLevel is set according to chosen WoundType
					// 'P''p' - level required
					// 'V''v' - level forbidden
					// 'E''e' - level allowed
					if (woundType.getLevelState() == WoundLevelState.REQUIRED) {
						if (level.getValue() == null) {
							Notification.show(MessageResources.getString("woundLevelRequired") + ".");
							return;
						}
					} else if (woundType.getLevelState() == WoundLevelState.FORBIDDEN) {
						if (level.getValue() != null) {
							Notification.show(MessageResources.getString("woundLevelForbidden") + ".");
							return;
						}
					}

				} else {
					Notification.show(MessageResources
							.getString("woundtypeSelectException") + ".");
					return;
				}

				// setDescription - is automatically cut to at most 2000
				// characters
				woundDescription.setDescription(comment.getValue());

				// setRecordingDate - all misformatted inputs result in "null"
				try {
					woundDescription.setDate(new java.sql.Date(recorded
							.getValue().getTime()));
				} catch (NullPointerException e) {
					Notification.show(MessageResources
							.getString("recordingDateFormatException"));
					e.printStackTrace();
					return;
				}

				// setSizes - if only one size is entered it is stored to size1
				// as diameter
				// - size must be between 0 and 9999
				try{
					if (size1.getValue().equals("") || size1.getValue().equals("0")){ //$NON-NLS-1$
						woundDescription.setSize2(0);
						if (size2.getValue().equals("") || size2.getValue().equals("0")){ //$NON-NLS-1$
							woundDescription.setSize1(0);
						} else {
							int size2Int = Integer.parseInt(size2.getValue());
							if (size2Int < 9999 && size2Int >= 0){
								woundDescription.setSize1(size2Int);
							} else {
								Notification.show(MessageResources.getString("sizeFormatException")); //$NON-NLS-1$
								return;
							}
						}
						
					} else {
						int size1Int = Integer.parseInt(size1.getValue());
						if (size1Int < 9999 && size1Int >= 0){
							woundDescription.setSize1(size1Int);
							if (size2.getValue().equals("")){ //$NON-NLS-1$
								woundDescription.setSize2(0);
							} else {
								int size2Int = Integer.parseInt(size2.getValue());
								if (size2Int < 9999 && size2Int >= 0){
									woundDescription.setSize2(size2Int);
								} else {
									Notification.show(MessageResources.getString("sizeFormatException")); //$NON-NLS-1$
									return;
								}
							} 
						} else {
							Notification.show(MessageResources.getString("sizeFormatException")); //$NON-NLS-1$
							return;
						}
					}
				} catch(NumberFormatException e){
					//should never get here actually
					Notification.show("Die Größe ist im falschen Format angegeben."); //$NON-NLS-1$
					e.printStackTrace();
					return;
				}

				// setDepth - depth must be between 0 and 99
				try {
					if (depth.getValue().equals("")) {
						woundDescription.setDepth(0);
					} else {
						int depthInt = Integer.parseInt(depth.getValue());
						if (depthInt < 99 && depthInt >= 0) {
							woundDescription.setDepth(depthInt);
						} else {
							Notification.show(MessageResources
									.getString("depthFormatException"));
							return;
						}
					}
				} catch (NumberFormatException e) {
					// should never get here actually
					Notification
							.show("Die Tiefe ist im falschen Format angegeben.");
					e.printStackTrace();
					return;
				}

				wound.getWoundDescriptions().add(woundDescription);
				WoundDescriptionProvider.getInstance().add(woundDescription);

				/*
				 * need a new view, just navigating back would get the user to
				 * the old view, which means the old list - new description
				 * would not be listed (even if created properly)
				 */
//				getNavigationManager().navigateTo(new WoundDescriptionListView(wound));
				Page.getCurrent().setUriFragment("woundDescriptions");
			}

		});

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();

		class ImageUploader implements Receiver, SucceededListener {

			private String filename = "";

			public OutputStream receiveUpload(String filename, String mimeType) {

				/*
				 * if the selected File is NOT an image (mimeType: image/*), do
				 * not upload.
				 */
				if (!(mimeType.startsWith("image"))) {
					Notification
							.show("Invalid file selected for Upload. Please only Upload Photos!");
					return null;
				}

				this.filename = filename;

				return bos;

			}

			public void uploadSucceeded(SucceededEvent event) {
				// if upload succeeded, add picture to database

				byte[] bFile = bos.toByteArray();

				woundDescription.setImage(bFile);
				Notification.show(MessageResources
						.getString("uploadsuccessful"));

				upload.setCaption(MessageResources
						.getString("uploadsuccessful") + " - " + filename);
				// After successful upload, the disable the button
				upload.setEnabled(false);

				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		;
		ImageUploader receiver = new ImageUploader();

		// if file upload was successful, the caption of the upload element will
		// change
		upload = new Upload("", receiver);
		upload.setImmediate(true);
		upload.setButtonCaption(MessageResources.getString("addpicture"));
		upload.addSucceededListener(receiver);

		VerticalLayout uploadLayout = new VerticalLayout();
		uploadLayout.addComponent(upload);

		mainLayout.addComponent(uploadLayout);

		mainLayout.addComponent(createNewDescription);

		setContent(mainLayout);
		
		String patientName = wound.getPatient().getFirstName() + " " + wound.getPatient().getLastName();
		BackButton backButton = new BackButton(MessageResources.getString("woundDescriptionsHeader") + " (" + patientName + ")", "woundDescriptions");
		setLeftComponent(backButton);
	}
}

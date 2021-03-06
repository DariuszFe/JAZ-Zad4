package com.example.jsfdemo.web;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.ListDataModel;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import com.example.jsfdemo.domain.Patient;
import com.example.jsfdemo.service.PatientManager;

@SessionScoped
@ManagedBean(name = "patientBean")
public class PatientFormBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private Patient patient = new Patient();
	private ListDataModel<Patient> patients = new ListDataModel<Patient>();
	
	@Inject
	private PatientManager pm;

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public ListDataModel<Patient> getAllPatients(){
		patients.setWrappedData(pm.getAllPatients());
		return patients;
	}
	
	public String addPatient(){
		pm.addPatient(patient);
		return "showPatients";
	}
	
	public String deletePatient(){
		Patient patientToDelete = patients.getRowData();
		pm.deletePatient(patientToDelete);
		return null;
	}
	
	public void uniquePin(FacesContext context, UIComponent component,
			Object value) {

		String pin = (String) value;

		for (Patient patient : pm.getAllPatients()) {
			if (patient.getPin().equalsIgnoreCase(pin)) {
				FacesMessage message = new FacesMessage(
						"Patient with this PIN already exists in database");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(message);
			}
		}
	}
	
	public void validatePinDob(ComponentSystemEvent event) {

		UIForm form = (UIForm) event.getComponent();
		UIInput pin = (UIInput) form.findComponent("pin");
		UIInput dob = (UIInput) form.findComponent("dob");

		if (pin.getValue() != null && dob.getValue() != null
				&& pin.getValue().toString().length() >= 2) {
			String twoDigitsOfPin = pin.getValue().toString().substring(0, 2);
			Calendar cal = Calendar.getInstance();
			cal.setTime(((Date) dob.getValue()));

			String lastDigitsOfDob = ((Integer) cal.get(Calendar.YEAR))
					.toString().substring(2);

			if (!twoDigitsOfPin.equals(lastDigitsOfDob)) {
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(form.getClientId(), new FacesMessage(
						"PIN doesn't match date of birth"));
				context.renderResponse();
			}
		}
	}
}



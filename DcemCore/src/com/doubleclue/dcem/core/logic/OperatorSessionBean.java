package com.doubleclue.dcem.core.logic;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.doubleclue.comm.thrift.AppSystemConstants;
import com.doubleclue.dcem.admin.logic.AdminModule;
import com.doubleclue.dcem.core.DcemConstants;
import com.doubleclue.dcem.core.as.AsModuleApi;
import com.doubleclue.dcem.core.as.AuthApplication;
import com.doubleclue.dcem.core.as.AuthMethod;
import com.doubleclue.dcem.core.as.AuthRequestParam;
import com.doubleclue.dcem.core.as.AuthenticateResponse;
import com.doubleclue.dcem.core.entities.DcemAction;
import com.doubleclue.dcem.core.entities.DcemGroup;
import com.doubleclue.dcem.core.entities.DcemRole;
import com.doubleclue.dcem.core.entities.DcemUser;
import com.doubleclue.dcem.core.entities.DcemUserExtension;
import com.doubleclue.dcem.core.entities.TenantEntity;
import com.doubleclue.dcem.core.exceptions.DcemErrorCodes;
import com.doubleclue.dcem.core.exceptions.DcemException;
import com.doubleclue.dcem.core.gui.DcemApplicationBean;
import com.doubleclue.dcem.core.gui.JsfUtils;
import com.doubleclue.dcem.core.gui.SupportedLanguage;
import com.doubleclue.dcem.core.gui.ViewNavigator;
import com.doubleclue.dcem.core.jpa.TenantIdResolver;
import com.doubleclue.dcem.core.weld.CdiUtils;
import com.doubleclue.utils.StringUtils;

@Named("operatorSession")
@SessionScoped
public class OperatorSessionBean implements Serializable {

	private static final Logger logger = LogManager.getLogger(OperatorSessionBean.class);

	@Inject
	DomainLogic domainLogic;

	@Inject
	ViewNavigator viewNavigator;

	@Inject
	DcemApplicationBean applicationBean;

	@Inject
	UserLogic userLogic;

	@Inject
	GroupLogic groupLogic;

	@Inject
	EntityManager em;

	@Inject
	AdminModule adminModule;

	private Set<DcemAction> haveAction;

	TenantEntity tenantEntity;

	AsModuleApi asModuleApi;

	String rolesText;

	DcemRole highestRole;
	
	List<DcemGroup> userGroups;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1754220879750612466L;

	boolean loggedIn;
	DcemUser dcemUser;

	boolean masterAdminGuest;


	@PostConstruct
	public void init() {
		asModuleApi = (AsModuleApi) CdiUtils.getReference(DcemConstants.AS_MODULE_API_IMPL_BEAN);
	}

	/**
	 * Check if at least one of the permission there
	 * 
	 * @param list
	 * @return
	 */
	public boolean isPermission(List<DcemAction> list) {
		if (dcemUser == null) { // Whats this?
			return true;
		}
		for (DcemAction dcemAction : list) {
			if (isPermission(dcemAction) == true) {
				return true;
			}
		}
		return false;
	}

	public boolean isPermission(DcemAction dcemAction) {
		if (dcemUser == null || dcemAction == null) {
			return false;
		}
		try {
			if (haveAction.contains(dcemAction) == true) {
				return true;
			}
		} catch (Exception e) {
			logger.warn(e);
		}
		return false;
	}

	public DcemAction getPermission(DcemAction dcemAction) {
		if (dcemUser == null || dcemAction == null) {
			return null;
		}
		try {
			for (DcemAction action : haveAction) {
				if (action.equals(dcemAction)) {
					return action;
				}
			}
		} catch (Exception e) {
			logger.warn(e);
		}
		return null;
	}

	public boolean isPermission(DcemAction dcemActionManage, DcemAction dcemAction) {
		return haveAction.contains(dcemActionManage) || haveAction.contains(dcemAction);
	}

	public boolean isUserLoggedInAndEnabled(HttpServletRequest httpServletRequest) {
		TenantEntity tenantEntity = (TenantEntity) httpServletRequest.getSession().getAttribute(DcemConstants.URL_TENANT_SWITCH);
		if (tenantEntity != null) {
			try {
				dcemUser = userLogic.getDistinctUser(DcemConstants.SUPER_ADMIN_OPERATOR);
				loggedInOperator(dcemUser, httpServletRequest);
				masterAdminGuest = true;
				httpServletRequest.getSession().removeAttribute(DcemConstants.URL_TENANT_SWITCH);
			} catch (DcemException e) {
				logger.warn("Couldn't swithc to Tenant", e);
				return false;
			}
		}

		if (loggedIn == false || dcemUser == null) {
			return false;
		}
		try {
			// This will be cached by Hazelcast
			dcemUser = userLogic.getUser(dcemUser.getId());
			return userLogic.isUserEnabled(dcemUser) == 0;
		} catch (Exception e) {
			logger.warn("Couldn't get user" + dcemUser.getDisplayNameOrLoginId(), e);
			return false;
		}
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	// verifyAndSetCredentials

	public boolean hasPermission(String permission) {
		// TODO
		return true;
	}

	// private void setMaxInactiveInterval(HttpServletRequest httpServletRequest) {
	// // String servletPath = httpServletRequest.getServletPath().toLowerCase();
	//
	// }

	public void loggedInOperator(DcemUser user, HttpServletRequest request) throws DcemException {
		if (user.isDisabled()) {
			logger.info("Operator is disabled. " + user.getLoginId());
			throw new DcemException(DcemErrorCodes.OPERATOR_IS_DISABLED, user.getLoginId());
		}

		// System.out.println("OperatorSessionBean.restLogin()tenant " + TenantIdResolver.getCurrentTenantName());
		Set<DcemRole> roles = new HashSet<>();
		highestRole = user.getDcemRole();
		roles.add(highestRole);
		userGroups = groupLogic.getAllUserGroups(user);
		for (DcemGroup group : userGroups) {
			if (group.getDcemRole() != null) {
				DcemRole groupRole = group.getDcemRole();
				if (groupRole.getRank() > highestRole.getRank()) {
					highestRole = groupRole;
				}
				roles.add(group.getDcemRole());
			}
		}
		if (highestRole.getRank() == 0) {
			throw new DcemException(DcemErrorCodes.NO_MANAGEMENT_RIGHTS, user.getLoginId());
		}
		SupportedLanguage supportedLocale = user.getLanguage();
		if (supportedLocale != null && FacesContext.getCurrentInstance() != null) {
			if (FacesContext.getCurrentInstance().getViewRoot() != null) {
				FacesContext.getCurrentInstance().getViewRoot().setLocale(supportedLocale.getLocale());
			}
		}
		this.dcemUser = user;
		haveAction = new HashSet<>();
		StringBuffer sb = new StringBuffer();
		for (DcemRole role : roles) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(role.getName());
			haveAction.addAll(role.getActions());
		}
		rolesText = sb.toString();
		loggedIn = true;
		tenantEntity = TenantIdResolver.getCurrentTenant();
		logger.info("Operator login: " + dcemUser.getLoginId());
		if (request == null) {
			request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			request.changeSessionId(); // to avoid Session hijacking
			request.getSession().setAttribute(DcemConstants.URL_TENANT_PARAMETER, tenantEntity);
			request.getSession().setMaxInactiveInterval(adminModule.getPreferences().getInactivityTimer() * 60);
		}
		return;
	}

	/*
	 *  Used from REST-API
	 * 
	 */
	public boolean restLogin(LoginAuthenticator loginAuthenticator) throws DcemException {

		String operatorName = loginAuthenticator.getName();
		String password = loginAuthenticator.getPassword();
		if (applicationBean.isMultiTenant()) {
			int ind = operatorName.lastIndexOf(AppSystemConstants.TENANT_SEPERATOR);
			if (ind > 0) {
				TenantEntity tenantEntity = applicationBean.getTenant(operatorName.substring(ind + 1));
				if (tenantEntity == null) {
					logger.info("Invalid Tenant Name: " + loginAuthenticator.getName());
					return false;
				}
				TenantIdResolver.setCurrentTenant(tenantEntity);
				operatorName = operatorName.substring(0, ind);
			}
		}
		AuthenticateResponse authenticateResponse = asModuleApi.authenticate(AuthApplication.WebServices, 0, operatorName, AuthMethod.PASSWORD, password, null,
				new AuthRequestParam());
		if (authenticateResponse.isSuccessful() == false) {
			return false;
		}
		dcemUser = authenticateResponse.getDcemUser();
		userLogic.setUserLogin(dcemUser);

		StringUtils.wipeString(password);
		password = null;
		loggedIn = true;
		haveAction = this.getDcemUser().getDcemRole().getActions();
		haveAction.iterator().hasNext();
		tenantEntity = TenantIdResolver.getCurrentTenant();
		return true;

	}

	public Locale getLocale() {
		if (dcemUser != null && dcemUser.getLanguage() != null) {
			return dcemUser.getLanguage().getLocale();
		}
		Locale locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
		return locale;
	}

	public TenantEntity getTenantEntity() {
		return tenantEntity;
	}

	public void setTenantEntity(TenantEntity tenantEntity) {
		this.tenantEntity = tenantEntity;
	}

	public DcemUser getDcemUser() {
		return dcemUser;
	}

	public void setDcemUser(DcemUser dcemUser) {
		this.dcemUser = dcemUser;
	}

	public String getDateTimeShortPattern() {
		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, getLocale());
		return ((SimpleDateFormat) formatter).toPattern();
	}

	public String getDateTimePattern() {
		DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, getLocale());
		return ((SimpleDateFormat) formatter).toPattern();
	}

	public String getRolesText() {
		return rolesText;
	}

	public void setRolesText(String rolesText) {
		this.rolesText = rolesText;
	}

	public DcemRole getHighestRole() {
		return highestRole;
	}

	public void setHighestRole(DcemRole highestRole) {
		this.highestRole = highestRole;
	}

	public StreamedContent getFotoProfileUser() {
		DcemUserExtension dcemUserExtension = dcemUser.getDcemUserExt();
		if (dcemUserExtension != null && dcemUserExtension.getPhoto() != null) {
			byte[] image = dcemUserExtension.getPhoto();
			InputStream in = new ByteArrayInputStream(image);
			return DefaultStreamedContent.builder().contentType("image/png").stream(() -> in).build();
		} else {
			return JsfUtils.getDefaultUserImage();
		}
	}

	public boolean isMasterAdminGuest() {
		return masterAdminGuest;
	}

	public void setMasterAdminGuest(boolean masterAdminGuest) {
		this.masterAdminGuest = masterAdminGuest;
	}

	
	public boolean isUserLoggedInAndEnabled() {
		return isUserLoggedInAndEnabled (null);
	}

	public List<DcemGroup> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(List<DcemGroup> userGroups) {
		this.userGroups = userGroups;
	}

}

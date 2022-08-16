package com.doubleclue.dcem.admin.logic;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.doubleclue.dcem.admin.preferences.AdminPreferences;
import com.doubleclue.dcem.core.DcemConstants;
import com.doubleclue.dcem.core.cluster.DcemCluster;
import com.doubleclue.dcem.core.entities.Auditing;
import com.doubleclue.dcem.core.entities.DcemReporting;
import com.doubleclue.dcem.core.entities.DcemUser;
import com.doubleclue.dcem.core.entities.TenantBrandingEntity;
import com.doubleclue.dcem.core.entities.TenantEntity;
import com.doubleclue.dcem.core.exceptions.DcemErrorCodes;
import com.doubleclue.dcem.core.exceptions.DcemException;
import com.doubleclue.dcem.core.gui.DcemApplicationBean;
import com.doubleclue.dcem.core.gui.DcemView;
import com.doubleclue.dcem.core.gui.JsfUtils;
import com.doubleclue.dcem.core.jpa.ExportRecords;
import com.doubleclue.dcem.core.jpa.TenantIdResolver;
import com.doubleclue.dcem.core.licence.LicenceLogic;
import com.doubleclue.dcem.core.logic.DomainLogic;
import com.doubleclue.dcem.core.logic.GroupLogic;
import com.doubleclue.dcem.core.logic.UserLogic;
import com.doubleclue.dcem.core.logic.module.DcemModule;
import com.doubleclue.dcem.core.logic.module.ModulePreferences;
import com.doubleclue.dcem.core.weld.CdiUtils;
import com.doubleclue.dcem.system.logic.SystemModule;
import com.doubleclue.utils.StringUtils;
import com.hazelcast.flakeidgen.FlakeIdGenerator;

@ApplicationScoped
@Named("adminModule")
public class AdminModule extends DcemModule {

	public final static String MODULE_ID = "admin";
	public final static String RESOURCE_NAME = "com.doubleclue.dcem.admin.resources.Messages";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger(AdminModule.class);

	@Inject
	TenantBrandingLogic tenantBrandingLogic;

	@Inject
	ExportRecords exportRecords;

	@Inject
	DcemApplicationBean applicationBean;

	@Inject
	DomainLogic domainLogic;

	@Inject
	protected EntityManager em;

	@Inject
	LicenceLogic licenceLogic;

	@Inject
	GroupLogic groupLogic;

	@Inject
	SystemModule systemModule;

	@Inject
	UserLogic userLogic;

	@Override
	public void start() throws DcemException {
		super.start();
		// policyLogic.reloadApplicationIdentifier();

	}

	// HashMap<String, SubjectAbs> subjects = new HashMap<String, SubjectAbs>();

	@Override
	public void init() throws DcemException {
		// ModuleDef moduleDef = new ModuleDef(MODULE_ID, "2.3", null);
		// moduleManifest = new ModuleManifest(moduleDef, "Administration", "", 20,
		// null, 1, true);
	}

	public String getResourceName() {
		return RESOURCE_NAME;
	}

	public String getName() {
		return "Administration";
	}

	@Override
	public String getId() {
		return MODULE_ID;
	}

	public int getRank() {
		return 20;
	}

	@Override
	public DcemView getDefaultView() {
		// TODO Auto-generated method stub
		try {
			return CdiUtils.getReference("welcomeView");
		} catch (Exception exp) {
			exp.printStackTrace();
			return null;
		}
	}

	public ModulePreferences getDefaultPreferences() {
		return new AdminPreferences();
	}

	public AdminTenantData getTenantData() {
		return (AdminTenantData) getModuleTenantData();
	}

	public String getTitle() {
		String name = "";
		String bannerTextEnterpriseManagment = getTenantData().getTenantBrandingEntity().getBannerTextEnterpriseManagment();
		if (bannerTextEnterpriseManagment != null && bannerTextEnterpriseManagment.isEmpty() == false) {
			return bannerTextEnterpriseManagment;
		} else {
			if (applicationBean.isMultiTenant()) {
				TenantEntity tenantEntity = TenantIdResolver.getCurrentTenant();
				if (tenantEntity.isMaster()) {
					if (DcemCluster.getInstance().getClusterConfig() != null) {
						name = DcemCluster.getInstance().getClusterConfig().getGivenName();
						if (name != null && name.isEmpty() == false) {
							name = " - " + name;
						} else {
							name = "";
						}
					}
				}
				name = name + " - " + tenantEntity.getName();
			}

			return DcemConstants.APP_TITLE + name;
		}
	}

	public String getTitleBannerTextUserPortal() {
		String name = "";
		String bannerTextUserPortal = getTenantData().getTenantBrandingEntity().getBannerTextUserPortal();
		if (bannerTextUserPortal != null && bannerTextUserPortal.isEmpty() == false) {
			return bannerTextUserPortal;
		} else {
			if (applicationBean.isMultiTenant()) {
				TenantEntity tenantEntity = TenantIdResolver.getCurrentTenant();
				if (tenantEntity.isMaster()) {
					if (DcemCluster.getInstance().getClusterConfig() != null) {
						name = DcemCluster.getInstance().getClusterConfig().getGivenName();
						if (name != null && name.isEmpty() == false) {
							name = " - " + name;
						} else {
							name = "";
						}
					}
				}
				name = name + " - " + tenantEntity.getName();
			}

			return DcemConstants.USERPORTAL_TITLE + name;
		}
	}

	public String getTitleEnterpriseManagment() {
		String name = "";
		String bannerTextEnterpriseManagment = getTenantData().getTenantBrandingEntity().getBannerTextEnterpriseManagment();
		if (bannerTextEnterpriseManagment != null && bannerTextEnterpriseManagment.isEmpty() == false) {
			return bannerTextEnterpriseManagment;
		} else {
			if (applicationBean.isMultiTenant()) {
				TenantEntity tenantEntity = TenantIdResolver.getCurrentTenant();
				if (tenantEntity.isMaster()) {
					if (DcemCluster.getInstance().getClusterConfig() != null) {
						name = DcemCluster.getInstance().getClusterConfig().getGivenName();
						if (name != null && name.isEmpty() == false) {
							name = " - " + name;
						} else {
							name = "";
						}
					}
				}
				name = name + " - " + tenantEntity.getName();
			}

			return DcemConstants.APP_TITLE + name;
		}
	}

	public String getSignInText() {
		return getTenantData().getTenantBrandingEntity().getSignInPageText();
	}

	public boolean isDefaultCompanyLogo() {
		return getTenantData().getTenantBrandingEntity().getCompanyLogo() == null;
	}

	public StreamedContent getLogo() {
		byte[] companyLogo = getTenantData().getTenantBrandingEntity().getCompanyLogo();
		if (companyLogo == null) {
			return JsfUtils.getEmptyImage();
		}
		return DefaultStreamedContent.builder().contentType("image/png").stream(() -> new ByteArrayInputStream(companyLogo)).build();
	}

	public boolean isDefaultBackgroundImg() {
		return getTenantData().getTenantBrandingEntity().getBackgroundImage() == null;
	}
	
	public boolean isBackgroundImage() {
		return getTenantData().getTenantBrandingEntity().getBackgroundImage() != null;
	}

	public StreamedContent getBackgroundImg() {
		byte[] backgroundimg = getTenantData().getTenantBrandingEntity().getBackgroundImage();
		if (backgroundimg == null) {
			return null;
		}
		return DefaultStreamedContent.builder().contentType("image/png").stream(() -> new ByteArrayInputStream(backgroundimg)).build();
	}

	public String getTitleStyle() {
		String style = getTenantData().getTenantBrandingEntity().getBannerStyleCSS();
		if (style == null) {
			return "";
		}
		return style;
	}

	public String getBannerStyleCSSUserPortal() {
		String style = getTenantData().getTenantBrandingEntity().getBannerStyleCSSUserPortal();
		if (style == null) {
			return "";
		}
		return style;
	}

	public String getTitleStyleSaml() {
		String style = getTenantData().getTenantBrandingEntity().getBannerStyleCSSsaml();
		if (style == null) {
			return "";
		}
		return style;
	}

	public String getTitleStyleOauth() {
		String style = getTenantData().getTenantBrandingEntity().getBannerStyleCSSOauth();
		if (style == null) {
			return "";
		}
		return style;
	}

	public String getSignInTextStyle() {
		String style = getTenantData().getTenantBrandingEntity().getSignTextStyleCSS();
		if (style == null) {
			return "";
		}
		return style;
	}

	public String getLoginBackgroundColor() {
		if (getTenantData().getTenantBrandingEntity().isBackgroundTypeColor() == true) {
			String backgroundColor = getTenantData().getTenantBrandingEntity().getBackgroundColor();
			String color = "background:#" + backgroundColor;
			return color;
		}
		return null;
	}

	@Override
	public boolean isHasDbTables() {
		return false;

	}

	public AdminPreferences getPreferences() {
		return ((AdminPreferences) getModulePreferences());
	}

	@Override
	public void runNightlyTask() {

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		// Archive Reports
		if (cal.get(Calendar.DAY_OF_MONTH) == 1 || systemModule.getSpecialPropery(DcemConstants.SPECIAL_PROPERTY_RUN_NIGHTLY_TASK) != null) {
			int days = getPreferences().getDurationForHistoryArchive();
			if (days > 0) {
				try {
					String[] result = exportRecords.archive(days, Auditing.class, Auditing.GET_AFTER, Auditing.DELETE_AFTER);
					if (result != null) {
						logger.info("Admin History-Archived: File=" + result[0] + " Records=" + result[1]);
					}
				} catch (DcemException exp) {
					logger.warn("Couldn't archive Admin-History", exp);
				}
			}

			days = getPreferences().getDurationForReportArchive();
			if (days > 0) {
				try {
					String[] result = exportRecords.archive(days, DcemReporting.class, DcemReporting.GET_AFTER, DcemReporting.DELETE_AFTER);
					if (result != null) {
						logger.info("AsReporting-Archived: File=" + result[0] + " Records=" + result[1]);
					}
				} catch (DcemException exp) {
					logger.warn("Couldn't archive Reports", exp);
				}
			}
		}
		// Check for any alerts regarding licence limits
		// licenceLogic.checkLicenceAlerts();
	}

	// @Override
	// public List<DcemReporting> getLicenceAlerts() {
	// return licenceLogic.getLicenceWarnings();
	// }

	public void initializeTenant(TenantEntity tenantEntity) throws DcemException {
		AdminTenantData adminTenantData = (AdminTenantData) moduleTenantMap.get(tenantEntity.getName());
		if (adminTenantData == null) {
			adminTenantData = new AdminTenantData();
			super.initializeTenant(tenantEntity, adminTenantData);
		}
		licenceLogic.loadLicenceKeyContent();
		String tenantName = tenantEntity.getName();
		DcemCluster dcemCluster = DcemCluster.getInstance();
		FlakeIdGenerator reportIdGenerator = dcemCluster.getIdGenerator("reportIdGenerator@" + tenantName);
		adminTenantData.setReportIdGenerator(reportIdGenerator);
		DcemUser superAdmin = userLogic.getDistinctUser(DcemConstants.SUPER_ADMIN_OPERATOR);
		if (superAdmin != null) {
			em.detach(superAdmin);
			StringUtils.wipeString(superAdmin.getInitialPassword());
			superAdmin.setHashPassword(null);
			adminTenantData.setSuperAdmin(superAdmin);
		}
		try {
			TenantBrandingEntity brandingEntity = tenantBrandingLogic.getTenantBrandingEntity();
			adminTenantData.setTenantBrandingEntity(brandingEntity);
			if (adminTenantData.getTenantBrandingEntity() == null) {
				adminTenantData.setTenantBrandingEntity(new TenantBrandingEntity());
			}
		} catch (Exception e) {
			logger.error("Could initialize Tenant Branding for " + tenantEntity.getName(), e);
		}
	}

	public AdminTenantData getAdminTenantData() {
		return (AdminTenantData) moduleTenantMap.get(TenantIdResolver.getCurrentTenantName());
	}

	public String getLocationType() {
		return getPreferences().getLocationInformation();
	}

	public String getLocationApi() {
		return "d1" + getPreferences().getLocationApiKey();
	}

	public void setLocationApi(String temp) {
		System.out.println("AdminModule.setLocationApi()");
	}

	public void setLocationType(String temp) {
	}

	public TimeZone getTimezone() {
		return TimeZone.getTimeZone(getTenantData().getTenantBrandingEntity().getTimezone());
	}
	
	public boolean isWindowsSso() {
		return getPreferences().isUseWindowsSSO();
	}

	@Override
	public void preferencesValidation(ModulePreferences modulePreferences) throws DcemException {
		AdminPreferences preferences = (AdminPreferences) modulePreferences;
		if (preferences.getAlertsNotificationGroup() != null) {
			if (groupLogic.getGroup(preferences.getAlertsNotificationGroup()) == null) {
				throw new DcemException(DcemErrorCodes.ALERT_NOTIFICATION_GROUP_NOT_FOUND, "Please enter a valid alert recipient group name");
			}
		}
	}

	public void actionRedirectionToUserPortal() {
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(DcemConstants.USER_PORTAL_WELCOME + DcemConstants.FACES_REDIRECT);
		} catch (IOException e) {
			logger.error("Could not redirect to Userportal", e);
		}
	}
}

package com.liferay.maven.plugins.util;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Author Jan Eerdekens (jan.eerdekens@gmail.com or Twitter @planetsizebrain)
 */
public class EntityResolver implements org.xml.sax.EntityResolver {

	private static final String HTTP_WITH_SLASH = "http://";
	private static final String HTTPS_WITH_SLASH = "https://";

	public InputSource resolveEntity(String publicId, String systemId) {
		ClassLoader classLoader = getClass().getClassLoader();

		if (publicId != null) {
			if (_PUBLIC_IDS.containsKey(publicId)) {
				String path = _PUBLIC_IDS.get(publicId);
				InputStream is = classLoader.getResourceAsStream(_DEFINITIONS_PATH + path);

				if (is == null) {
					is = classLoader.getResourceAsStream(path);
				}

				return new InputSource(is);
			}
		} else if (systemId != null) {
			if (_SYSTEM_IDS.containsKey(systemId)) {
				String path = _SYSTEM_IDS.get(systemId);
				InputStream is = classLoader.getResourceAsStream(_DEFINITIONS_PATH + path);

				if (is == null) {
					is = classLoader.getResourceAsStream(path);
				}

				InputSource inputSource = new InputSource(is);

				inputSource.setSystemId(systemId);

				return inputSource;
			}

			if (!systemId.endsWith(".dtd") && !systemId.endsWith(".xsd")) {
				throw new XNIException("Invalid system id " + systemId);
			}

			if (!systemId.startsWith(HTTP_WITH_SLASH) && !systemId.startsWith(HTTPS_WITH_SLASH)) {

				InputStream inputStream = classLoader.getResourceAsStream(systemId);

				if (inputStream != null) {
					InputSource inputSource = new InputSource(inputStream);

					inputSource.setSystemId(systemId);

					return inputSource;
				} else {
					throw new XNIException("Invalid system id " + systemId);
				}
			}
		}

		return null;
	}

	private static final String _DEFINITIONS_PATH = "com/liferay/portal/definitions/";

	private static final Map<String, String> _PUBLIC_IDS = new HashMap<String, String>(128);

	private static final Map<String, String> _SYSTEM_IDS = new HashMap<String, String>(32);

	static {
		_PUBLIC_IDS.put("datatypes", "datatypes.dtd");

		_PUBLIC_IDS.put("-//Sun Microsystems, Inc.//DTD Facelet Taglib 1.0//EN", "facelet-taglib_1_0.dtd");

		_PUBLIC_IDS.put("-//Hibernate/Hibernate Mapping DTD 3.0//EN", "hibernate-mapping-3.0.dtd");

		_PUBLIC_IDS.put("-//Liferay//DTD Display 2.0.0//EN", "liferay-display_2_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Display 3.5.0//EN", "liferay-display_3_5_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Display 4.0.0//EN", "liferay-display_4_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Display 5.0.0//EN", "liferay-display_5_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Display 5.1.0//EN", "liferay-display_5_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Display 5.2.0//EN", "liferay-display_5_2_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Display 6.0.0//EN", "liferay-display_6_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Display 6.1.0//EN", "liferay-display_6_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Display 6.2.0//EN", "liferay-display_6_2_0.dtd");

		_PUBLIC_IDS.put("-//Liferay//DTD Friendly URL Routes 6.0.0//EN", "liferay-friendly-url-routes_6_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Friendly URL Routes 6.1.0//EN", "liferay-friendly-url-routes_6_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Friendly URL Routes 6.2.0//EN", "liferay-friendly-url-routes_6_2_0.dtd");

		_PUBLIC_IDS.put("-//Liferay//DTD Hook 5.1.0//EN", "liferay-hook_5_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Hook 5.2.0//EN", "liferay-hook_5_2_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Hook 6.0.0//EN", "liferay-hook_6_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Hook 6.1.0//EN", "liferay-hook_6_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Hook 6.2.0//EN", "liferay-hook_6_2_0.dtd");

		_PUBLIC_IDS.put("-//Liferay//DTD Layout Templates 3.6.0//EN", "liferay-layout-templates_3_6_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Layout Templates 4.0.0//EN", "liferay-layout-templates_4_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Layout Templates 4.3.0//EN", "liferay-layout-templates_4_3_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Layout Templates 5.0.0//EN", "liferay-layout-templates_5_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Layout Templates 5.1.0//EN", "liferay-layout-templates_5_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Layout Templates 5.2.0//EN", "liferay-layout-templates_5_2_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Layout Templates 6.0.0//EN", "liferay-layout-templates_6_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Layout Templates 6.1.0//EN", "liferay-layout-templates_6_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Layout Templates 6.2.0//EN", "liferay-layout-templates_6_2_0.dtd");

		_PUBLIC_IDS.put("-//Liferay//DTD Look and Feel 3.5.0//EN", "liferay-look-and-feel_3_5_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Look and Feel 4.0.0//EN", "liferay-look-and-feel_4_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Look and Feel 4.3.0//EN", "liferay-look-and-feel_4_3_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Look and Feel 5.0.0//EN", "liferay-look-and-feel_5_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Look and Feel 5.1.0//EN", "liferay-look-and-feel_5_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Look and Feel 5.2.0//EN", "liferay-look-and-feel_5_2_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Look and Feel 6.0.0//EN", "liferay-look-and-feel_6_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Look and Feel 6.1.0//EN", "liferay-look-and-feel_6_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Look and Feel 6.2.0//EN", "liferay-look-and-feel_6_2_0.dtd");

		_PUBLIC_IDS.put("-//Liferay//DTD Plugin Package 4.3.0//EN", "liferay-plugin-package_4_3_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Plugin Package 5.0.0//EN", "liferay-plugin-package_5_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Plugin Package 5.1.0//EN", "liferay-plugin-package_5_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Plugin Package 5.2.0//EN", "liferay-plugin-package_5_2_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Plugin Package 6.0.0//EN", "liferay-plugin-package_6_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Plugin Package 6.1.0//EN", "liferay-plugin-package_6_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Plugin Package 6.2.0//EN", "liferay-plugin-package_6_2_0.dtd");

		_PUBLIC_IDS.put("-//Liferay//DTD Plugin Repository 4.3.0//EN", "liferay-plugin-repository_4_3_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Plugin Repository 5.0.0//EN", "liferay-plugin-repository_5_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Plugin Repository 5.1.0//EN", "liferay-plugin-repository_5_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Plugin Repository 5.2.0//EN", "liferay-plugin-repository_5_2_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Plugin Repository 6.0.0//EN", "liferay-plugin-repository_6_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Plugin Repository 6.1.0//EN", "liferay-plugin-repository_6_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Plugin Repository 6.2.0//EN", "liferay-plugin-repository_6_2_0.dtd");

		_PUBLIC_IDS.put("-//Liferay//DTD Portlet Application 3.5.0//EN", "liferay-portlet-app_3_5_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Portlet Application 4.0.0//EN", "liferay-portlet-app_4_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Portlet Application 4.1.0//EN", "liferay-portlet-app_4_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Portlet Application 4.2.0//EN", "liferay-portlet-app_4_2_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Portlet Application 4.3.0//EN", "liferay-portlet-app_4_3_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Portlet Application 4.3.1//EN", "liferay-portlet-app_4_3_1.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Portlet Application 4.3.2//EN", "liferay-portlet-app_4_3_2.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Portlet Application 4.3.3//EN", "liferay-portlet-app_4_3_3.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Portlet Application 4.3.6//EN", "liferay-portlet-app_4_3_6.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Portlet Application 4.4.0//EN", "liferay-portlet-app_4_4_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Portlet Application 5.0.0//EN", "liferay-portlet-app_5_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Portlet Application 5.1.0//EN", "liferay-portlet-app_5_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Portlet Application 5.2.0//EN", "liferay-portlet-app_5_2_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Portlet Application 6.0.0//EN", "liferay-portlet-app_6_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Portlet Application 6.1.0//EN", "liferay-portlet-app_6_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Portlet Application 6.2.0//EN", "liferay-portlet-app_6_2_0.dtd");

		_PUBLIC_IDS.put("-//Liferay//DTD Resource Action Mapping 6.0.0//EN", "liferay-resource-action-mapping_6_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Resource Action Mapping 6.1.0//EN", "liferay-resource-action-mapping_6_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Resource Action Mapping 6.2.0//EN", "liferay-resource-action-mapping_6_2_0.dtd");

		_PUBLIC_IDS.put("-//Liferay//DTD Service Builder 3.5.0//EN", "liferay-service-builder_3_5_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Service Builder 3.6.1//EN", "liferay-service-builder_3_6_1.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Service Builder 4.0.0//EN", "liferay-service-builder_4_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Service Builder 4.2.0//EN", "liferay-service-builder_4_2_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Service Builder 4.3.0//EN", "liferay-service-builder_4_3_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Service Builder 4.3.3//EN", "liferay-service-builder_4_3_3.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Service Builder 4.4.0//EN", "liferay-service-builder_4_4_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Service Builder 5.0.0//EN", "liferay-service-builder_5_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Service Builder 5.1.0//EN", "liferay-service-builder_5_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Service Builder 5.2.0//EN", "liferay-service-builder_5_2_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Service Builder 6.0.0//EN", "liferay-service-builder_6_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Service Builder 6.1.0//EN", "liferay-service-builder_6_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Service Builder 6.2.0//EN", "liferay-service-builder_6_2_0.dtd");

		_PUBLIC_IDS.put("-//Liferay//DTD Social 6.1.0//EN", "liferay-social_6_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Social 6.2.0//EN", "liferay-social_6_2_0.dtd");

		_PUBLIC_IDS.put("-//Liferay//DTD Theme Loader 4.3.0//EN", "liferay-theme-loader_4_3_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Theme Loader 5.0.0//EN", "liferay-theme-loader_5_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Theme Loader 5.1.0//EN", "liferay-theme-loader_5_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Theme Loader 5.2.0//EN", "liferay-theme-loader_5_2_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Theme Loader 6.0.0//EN", "liferay-theme-loader_6_0_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Theme Loader 6.1.0//EN", "liferay-theme-loader_6_1_0.dtd");
		_PUBLIC_IDS.put("-//Liferay//DTD Theme Loader 6.2.0//EN", "liferay-theme-loader_6_2_0.dtd");

		_PUBLIC_IDS.put("-//MuleSource //DTD mule-configuration XML V1.0//EN", "mule-configuration.dtd");

		_PUBLIC_IDS.put("-//SPRING//DTD BEAN//EN", "spring-beans.dtd");

		_PUBLIC_IDS.put("-//Apache Software Foundation//DTD Struts Configuration 1.2//EN", "struts-config_1_2.dtd");

		_PUBLIC_IDS.put("-//Apache Software Foundation//DTD Tiles Configuration 1.1//EN", "tiles-config_1_1.dtd");

		_PUBLIC_IDS.put("-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN", "web-app_2_3.dtd");

		_PUBLIC_IDS.put("-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.0//EN", "web-facesconfig_1_0.dtd");

		_PUBLIC_IDS.put("-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.1//EN", "web-facesconfig_1_1.dtd");

		_PUBLIC_IDS.put("-//W3C//DTD XMLSCHEMA 200102//EN", "XMLSchema.dtd");



		_SYSTEM_IDS.put("http://java.sun.com/xml/ns/j2ee/j2ee_1_4.xsd", "j2ee_1_4.xsd");

		_SYSTEM_IDS.put("http://www.ibm.com/webservices/xsd/" + "j2ee_web_services_client_1_1.xsd", "j2ee_web_services_client_1_1.xsd");

		_SYSTEM_IDS.put("http://java.sun.com/xml/ns/javaee/javaee_5.xsd", "javaee_5.xsd");
		_SYSTEM_IDS.put("http://java.sun.com/xml/ns/javaee/javaee_6.xsd", "javaee_6.xsd");

		_SYSTEM_IDS.put("http://java.sun.com/xml/ns/javaee/" + "javaee_web_services_client_1_2.xsd", "javaee_web_services_client_1_2.xsd");
		_SYSTEM_IDS.put("http://java.sun.com/xml/ns/javaee/" + "javaee_web_services_client_1_3.xsd", "javaee_web_services_client_1_3.xsd");

		_SYSTEM_IDS.put("http://java.sun.com/xml/ns/j2ee/jsp_2_0.xsd", "jsp_2_0.xsd");
		_SYSTEM_IDS.put("http://java.sun.com/xml/ns/javaee/jsp_2_1.xsd", "jsp_2_1.xsd");
		_SYSTEM_IDS.put("http://java.sun.com/xml/ns/javaee/jsp_2_2.xsd", "jsp_2_2.xsd");

		_SYSTEM_IDS.put("http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd", "portlet-app_1_0.xsd");
		_SYSTEM_IDS.put("http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd", "portlet-app_2_0.xsd");

		_SYSTEM_IDS.put("http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd", "web-app_2_4.xsd");
		_SYSTEM_IDS.put("http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd", "web-app_2_5.xsd");
		_SYSTEM_IDS.put("http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd", "web-app_3_0.xsd");

		_SYSTEM_IDS.put("http://java.sun.com/xml/ns/javaee/web-common_3_0.xsd", "web-common_3_0.xsd");

		_SYSTEM_IDS.put("http://java.sun.com/xml/ns/javaee/web-facesconfig_1_2.xsd", "web-facesconfig_1_2.xsd");
		_SYSTEM_IDS.put("http://java.sun.com/xml/ns/javaee/web-facesconfig_2_0.xsd", "web-facesconfig_2_0.xsd");
		_SYSTEM_IDS.put("http://java.sun.com/xml/ns/javaee/web-facesconfig_2_1.xsd", "web-facesconfig_2_1.xsd");

		_SYSTEM_IDS.put("http://www.liferay.com/dtd/liferay-ddm-structure_6_2_0.xsd", "liferay-ddm-structure_6_2_0.xsd");

		_SYSTEM_IDS.put("http://www.liferay.com/dtd/liferay-workflow-definition_6_0_0.xsd", "liferay-workflow-definition_6_0_0.xsd");
		_SYSTEM_IDS.put("http://www.liferay.com/dtd/liferay-workflow-definition_6_1_0.xsd", "liferay-workflow-definition_6_1_0.xsd");
		_SYSTEM_IDS.put("http://www.liferay.com/dtd/liferay-workflow-definition_6_2_0.xsd", "liferay-workflow-definition_6_2_0.xsd");

		_SYSTEM_IDS.put("http://www.w3.org/2001/xml.xsd", "xml.xsd");
	}
}
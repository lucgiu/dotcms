package it.lucgiu.viewtools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.HashMap;
import java.util.Iterator;




import org.apache.commons.beanutils.BeanUtils;
import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.Permission;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.cache.FieldsCache;
import com.dotmarketing.cache.StructureCache;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.plugin.business.PluginAPI;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.business.DotContentletValidationException;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.files.model.File;
import com.liferay.portal.model.User;
import com.liferay.portal.ejb.UserLocalManagerUtil;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.portlets.structure.business.FieldAPI;
import com.dotmarketing.portlets.structure.factories.FieldFactory;
import com.dotmarketing.portlets.structure.model.Field;

/**
 * Simple Add Contetlet 
 * 
 * @author Luca Giustiniani
 * @version 2012.0001
 */
public class AddContetletTool implements ViewTool {

	private PermissionAPI permissionAPI;
	private ContentletAPI conAPI;
	private List<Permission> structurePermissions;
	private FieldAPI fAPI;

	
	
		public String updateContentlet(long structure, String idUser,boolean status,String identifier, List<String> key , List<String> value ) {


		/* First we need the plugin Properties */
		PluginAPI pluginAPI = APILocator.getPluginAPI();
		String debug="";
		
		conAPI = APILocator.getContentletAPI();
		permissionAPI = APILocator.getPermissionAPI();
		fAPI = APILocator.getFieldAPI();
		
		User user = null;
		Structure st=null;
		Contentlet newCont = new Contentlet();
		
		try {
			user  = UserLocalManagerUtil.getUserById(idUser);
			debug= "-100";
		} catch(Exception ex) {
			return debug + ex.toString();
		}
	
		
		try {
			 st = StructureCache.getStructureByInode (structure);
			structurePermissions = permissionAPI.getPermissions(st);
			debug = "-200 ";
		} catch(Exception ex) {
			return debug + ex.toString();
		}
	
		try {
			if (!(identifier.equals("0") || identifier.equals("") || identifier == null)){
				debug = "-210";
				newCont.setIdentifier(Long.parseLong(identifier));
			}
			debug = "-220";
			newCont.setLive(status);
			newCont.setWorking(true);
			newCont.setStructureInode(st.getInode());
			newCont.setLanguageId(1);
			
			List<Field> list = (List<Field>) FieldsCache.getFieldsByStructureInode(st.getInode());
			debug = "-300";

			 for (Field field : list) {
                 for (int i=0; i<key.size();i++){      
                          System.out.println( "Chiave: "+ key.get(i)+" Valore: "+ value.get(i) );
                          if (field.getFieldName().equalsIgnoreCase(key.get(i))){
                                conAPI.setContentletProperty(newCont, field, value.get(i));
                          }
                    
               }
               }
		
		} catch(Exception ex) {
			return "-400" + ex.toString();
		}
		
		
		//conAPI.checkin(cont, new ArrayList<Category>(categories), structurePermissions, user, false);
		try {
			conAPI.checkin(newCont, null, structurePermissions, user, false);
		} catch(Exception ex) {
			return "-500" + ex.toString();
		}
		return "OK";
	}

		public String addContentlet(long structure, String idUser, boolean status, List<String> key , List<String> value ) {
			return updateContentlet(structure, idUser,status,"0", key , value );
		}
	/**
	 * Init Method for the viewtool
	 */
	public void init(Object obj) {

	}
}

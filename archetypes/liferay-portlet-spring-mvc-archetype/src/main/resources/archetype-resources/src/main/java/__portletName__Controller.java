package ${package};


import java.util.List;


import javax.portlet.RenderRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;



@Controller("${portletName}Controller")
@RequestMapping("VIEW")
public class ${portletName}Controller {
	
	
	
	@RenderMapping
	public ModelAndView onPortletRender(RenderRequest request) {

		ModelAndView res = new ModelAndView();
		res.setViewName("view");

		
		return res;
	}

	
}
package org.jahia.modules.mp3Player;

import net.htmlparser.jericho.*;
import org.apache.commons.lang.StringUtils;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.services.render.filter.cache.AggregateCacheFilter;
import org.jahia.services.templates.JahiaTemplateManagerService.TemplatePackageRedeployedEvent;
import org.jahia.utils.ScriptEngineUtils;
import org.jahia.utils.WebUtils;
import org.slf4j.*;
import org.slf4j.Logger;
import org.jahia.services.render.filter.AbstractFilter;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleScriptContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: pol
 * Date: 04.05.12
 * Time: 09:38
 * To change this template use File | Settings | File Templates.
 */
public class MP3PlayerFilter extends AbstractFilter implements ApplicationListener<ApplicationEvent> {
    private static Logger logger = LoggerFactory.getLogger(MP3PlayerFilter.class);
    private ScriptEngineUtils scriptEngineUtils;
    private String template;
    private String resolvedTemplate;

    public String execute(String previousOut, RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        String out = previousOut;


        String leftbg = renderContext.getSite().hasProperty("leftbg") ? renderContext.getSite().getProperty("leftbg").getString() : "CCCCCC";
        String rightbg = renderContext.getSite().hasProperty("rightbg") ? renderContext.getSite().getProperty("rightbg").getString() : "0099FF";
        String rightbghover = renderContext.getSite().hasProperty("rightbghover") ? renderContext.getSite().getProperty("rightbghover").getString() : "008ae5";
        String lefticon = renderContext.getSite().hasProperty("lefticon") ? renderContext.getSite().getProperty("lefticon").getString() : "FFFFFF";
        String righticon = renderContext.getSite().hasProperty("righticon") ? renderContext.getSite().getProperty("righticon").getString() : "FFFFFF";
        String height = renderContext.getSite().hasProperty("height") ? renderContext.getSite().getProperty("height").getString() : "50";

        String script = getResolvedTemplate();

        if (script != null) {
            Source source = new Source(previousOut);
            OutputDocument outputDocument = new OutputDocument(source);

            List<Element> headElementList = source.getAllElements(HTMLElementName.BODY);
            for (Element element : headElementList) {
                final EndTag bodyEndTag = element.getEndTag();
                String extension = StringUtils.substringAfterLast(template, ".");
                ScriptEngine scriptEngine = scriptEngineUtils.scriptEngine(extension);
                ScriptContext scriptContext = new MP3PlayerScriptContext();

                final Bindings bindings = scriptEngine.createBindings();
                bindings.put("leftbg", leftbg);
                bindings.put("rightbg", rightbg);
                bindings.put("rightbghover", rightbghover);
                bindings.put("lefticon", lefticon);
                bindings.put("righticon", righticon);
                bindings.put("height", height);

                scriptContext.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
                // The following binding is necessary for Javascript, which doesn't offer a console by default.
                bindings.put("out", new PrintWriter(scriptContext.getWriter()));
                scriptEngine.eval(script, scriptContext);

                StringWriter writer = (StringWriter) scriptContext.getWriter();
                final String mp3PlayerScript = writer.toString();

                logger.error("mp3PlayerScript is " +AggregateCacheFilter.removeEsiTags(mp3PlayerScript));

                if (StringUtils.isNotBlank(mp3PlayerScript )) {
                    outputDocument.replace(bodyEndTag.getBegin(), bodyEndTag.getBegin() + 1,
                            "\n" + AggregateCacheFilter.removeEsiTags(mp3PlayerScript) + "\n<");
                }
                break; // avoid to loop if for any reasons multiple body in the page

            }
            out = outputDocument.toString().trim();
        }
        return out;
    }
    protected String getResolvedTemplate() throws IOException {
        if (resolvedTemplate == null) {
            resolvedTemplate = WebUtils.getResourceAsString(template);
            if (resolvedTemplate == null) {
                logger.warn("Unable to lookup template at {}", template);
            }
        }
        return resolvedTemplate;
    }

    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof TemplatePackageRedeployedEvent) {
            resolvedTemplate = null;
        }
    }

    public void setScriptEngineUtils(ScriptEngineUtils scriptEngineUtils) {
        this.scriptEngineUtils = scriptEngineUtils;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    class MP3PlayerScriptContext extends SimpleScriptContext {
        private Writer writer = null;

        /**
         * {@inheritDoc}
         */
        @Override
        public Writer getWriter() {
            if (writer == null) {
                writer = new StringWriter();
            }
            return writer;
        }
    }
}

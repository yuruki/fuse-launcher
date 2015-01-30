package com.github.yuruki;

import java.util.Map;
import java.util.regex.Pattern;

import io.fabric8.api.Container;
import io.fabric8.api.FabricService;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(label = FuseLauncher.COMPONENT_LABEL, description = FuseLauncher.COMPONENT_DESCRIPTION, immediate = true, metatype = true)
@Properties({
    @Property(name = "ChildPattern", value = "^(broker|camel).*$", description = "Child containers matching this regex will be started")
})
public class FuseLauncher {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String COMPONENT_LABEL = "FuseLauncher";
    public static final String COMPONENT_DESCRIPTION = "This is the current configuration for fuse-launcher.";

    private Pattern pattern = null;

    // Wait for FabricService
    @Reference(referenceInterface = FabricService.class, bind = "gotFabricService", unbind = "lostFabricService", policy = ReferencePolicy.DYNAMIC)
    FabricService fabricService;

    @Activate
    public void activate(final BundleContext bundleContext, final Map<String, String> props) throws Exception {
        pattern = Pattern.compile(props.get("ChildPattern"));
        for (Container c : fabricService.getCurrentContainer().getChildren()) {
            if (pattern.matcher(c.getId()).matches()) {
                log.info("Starting container {}", c.getId());
                fabricService.startContainer(c, true);
            }
        }
    }

    @Deactivate
    public void deactivate() {
        if (null != fabricService) {
            for (Container c : fabricService.getCurrentContainer().getChildren()) {
                if (pattern.matcher(c.getId()).matches()) {
                    log.info("Stopping container {}", c.getId());
                    fabricService.stopContainer(c, true);
                }
            }
        }
    }

    protected void gotFabricService(FabricService f) {
        fabricService = f;
    }

    public void lostFabricService(FabricService f) {
        fabricService = null;
        for (Container c : f.getCurrentContainer().getChildren()) {
            if (pattern.matcher(c.getId()).matches()) {
                log.info("Stopping container {}", c.getId());
                f.stopContainer(c, true);
            }
        }
    }
}
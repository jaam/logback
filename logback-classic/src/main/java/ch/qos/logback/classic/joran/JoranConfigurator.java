/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.classic.joran;

import ch.qos.logback.classic.joran.action.ConfigurationAction;
import ch.qos.logback.classic.joran.action.ConsolePluginAction;
import ch.qos.logback.classic.joran.action.ContextNameAction;
import ch.qos.logback.classic.joran.action.InsertFromJNDIAction;
import ch.qos.logback.classic.joran.action.LevelAction;
import ch.qos.logback.classic.joran.action.LoggerAction;
import ch.qos.logback.classic.joran.action.LoggerContextListenerAction;
import ch.qos.logback.classic.joran.action.ReceiverAction;
import ch.qos.logback.classic.joran.action.RootLoggerAction;
import ch.qos.logback.classic.joran.sanity.IfNestedWithinSecondPhaseElementSC;
import ch.qos.logback.classic.model.processor.LogbackClassicDefaultNestedComponentRules;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.JoranConfiguratorBase;
import ch.qos.logback.core.joran.action.AppenderRefAction;
import ch.qos.logback.core.joran.action.IncludeAction;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.RuleStore;
import ch.qos.logback.core.model.Model;
import ch.qos.logback.core.model.processor.DefaultProcessor;

/**
 * JoranConfigurator class adds rules specific to logback-classic.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class JoranConfigurator extends JoranConfiguratorBase<ILoggingEvent> {

    @Override
    public void addElementSelectorAndActionAssociations(RuleStore rs) {
        // add parent rules
        super.addElementSelectorAndActionAssociations(rs);

        rs.addRule(new ElementSelector("configuration"), () -> new ConfigurationAction());

        rs.addRule(new ElementSelector("configuration/contextName"), () -> new ContextNameAction());
        rs.addRule(new ElementSelector("configuration/contextListener"), () -> new LoggerContextListenerAction());
        rs.addRule(new ElementSelector("configuration/insertFromJNDI"), () -> new InsertFromJNDIAction());

        rs.addRule(new ElementSelector("configuration/logger"), () -> new LoggerAction());
        rs.addRule(new ElementSelector("configuration/logger/level"), () -> new LevelAction());

        rs.addRule(new ElementSelector("configuration/root"), () -> new RootLoggerAction());
        rs.addRule(new ElementSelector("configuration/root/level"), () -> new LevelAction());
        rs.addRule(new ElementSelector("configuration/logger/appender-ref"), () -> new AppenderRefAction());
        rs.addRule(new ElementSelector("configuration/root/appender-ref"), () -> new AppenderRefAction());

        rs.addRule(new ElementSelector("configuration/include"), () -> new IncludeAction());

        rs.addRule(new ElementSelector("configuration/consolePlugin"), () -> new ConsolePluginAction());

        rs.addRule(new ElementSelector("configuration/receiver"), () -> new ReceiverAction());

    }


    @Override
    protected void sanityCheck(Model topModel) {
        super.sanityCheck(topModel);
        performCheck(new IfNestedWithinSecondPhaseElementSC(), topModel);
    }

    @Override
    protected void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry registry) {
        LogbackClassicDefaultNestedComponentRules.addDefaultNestedComponentRegistryRules(registry);
    }

    @Override
    protected void addModelHandlerAssociations(DefaultProcessor defaultProcessor) {
        ModelClassToModelHandlerLinker m = new ModelClassToModelHandlerLinker(context);
        m.link(defaultProcessor);
    }


    // The final filters in the two filter chain are rather crucial.
    // They ensure that only Models attached to the firstPhaseFilter will
    // be handled in the first phase and all models not previously handled
    // in the second phase will be handled in a catch-all fallback case.
    private void sealModelFilters(DefaultProcessor defaultProcessor) {
        defaultProcessor.getPhaseOneFilter().denyAll();
        defaultProcessor.getPhaseTwoFilter().allowAll();
    }

}

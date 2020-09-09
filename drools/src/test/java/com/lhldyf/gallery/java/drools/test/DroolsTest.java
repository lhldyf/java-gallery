package com.lhldyf.gallery.java.drools.test;

import com.lhldyf.gallery.java.drools.entity.IDSEntity;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;

/**
 * @author lhldyf
 * @date 2019-06-30 21:56
 */
public class DroolsTest {

    @Test
    public void stateful() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        KieSession kSession = kContainer.newKieSession("ksession-rules");

        IDSEntity entity = new IDSEntity();
        entity.setLevel(10);
        entity.setType("boc");
        kSession.insert(entity);
        kSession.fireAllRules();
        System.out.println("score: " + entity.getScore());
        entity.setLevel(3);
        IDSEntity entity1 = new IDSEntity();
        entity1.setLevel(3);
        entity1.setType("boc");
        kSession.insert(entity1);
        entity.setLevel(4);
        kSession.update(kSession.getFactHandle(entity), entity);
        kSession.fireAllRules();
        kSession.dispose();
        kSession.insert(entity);
        kSession.fireAllRules();
        System.out.println("score: " + entity1.getScore());
        System.out.println("score: " + entity.getScore());
    }

    @Test
    public void stateless() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        StatelessKieSession kSession = kContainer.newStatelessKieSession("stateless-rules");

        IDSEntity entity = new IDSEntity();
        entity.setLevel(10);
        entity.setType("boc");
        kSession.execute(entity);
        System.out.println("score: " + entity.getScore());
        entity.setLevel(3);
        IDSEntity entity1 = new IDSEntity();
        entity1.setLevel(3);
        entity1.setType("boc");
        kSession.execute(entity1);
        entity.setLevel(4);
        kSession.execute(entity);
        System.out.println("score: " + entity1.getScore());
        System.out.println("score: " + entity.getScore());
    }
}

package gate.creole;

import static org.junit.Assert.*;

import gate.Factory;
import gate.Gate;
import gate.LanguageResource;
import gate.ProcessingResource;
import gate.Resource;
import gate.VisualResource;
import gate.corpora.CorpusImpl;
import gate.event.CreoleListener;
import gate.event.PluginListener;
import gate.gui.PRViewer;
import gate.util.GateException;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

public class CreoleRegisterImplPromptTest {

  @Test
  public void newRegisterStartsEmptyAndExposesReadOnlyViews() throws Exception {
    if(!Gate.isInitialised()) Gate.init();
    CreoleRegisterImpl register = new CreoleRegisterImpl();

    assertTrue(register.isEmpty());
    assertTrue(register.getDirectories().isEmpty());
    assertTrue(register.getPlugins().isEmpty());
    assertTrue(register.getLrTypes().isEmpty());
    assertTrue(register.getPrTypes().isEmpty());
    assertTrue(register.getVrTypes().isEmpty());
    assertTrue(register.getToolTypes().isEmpty());
    assertTrue(register.getApplicationTypes().isEmpty());
    try {
      register.getLrTypes().add("not.allowed");
      fail("type view must be immutable");
    } catch(UnsupportedOperationException expected) {
      assertTrue(register.getLrTypes().isEmpty());
    }
    try {
      register.getPlugins().clear();
      fail("plugin view must be immutable");
    } catch(UnsupportedOperationException expected) {
      assertTrue(register.getPlugins().isEmpty());
    }
  }

  @Test
  public void putIndexesLanguageResourceAndToolAndRemoveCleansIndexes()
      throws Exception {
    if(!Gate.isInitialised()) Gate.init();
    CreoleRegisterImpl register = new CreoleRegisterImpl();
    ResourceData data = new ResourceData();
    data.setName("Corpus");
    data.setClassName(CorpusImpl.class.getName());
    data.setResourceClass(CorpusImpl.class);
    data.setTool(true);

    assertNull(register.put(CorpusImpl.class.getName(), data));
    assertSame(data, register.get(CorpusImpl.class.getName()));
    assertTrue(register.getLrTypes().contains(CorpusImpl.class.getName()));
    assertTrue(register.getToolTypes().contains(CorpusImpl.class.getName()));
    assertFalse(register.getPrTypes().contains(CorpusImpl.class.getName()));

    assertSame(data, register.remove(CorpusImpl.class.getName()));
    assertFalse(register.containsKey(CorpusImpl.class.getName()));
    assertFalse(register.getLrTypes().contains(CorpusImpl.class.getName()));
    assertFalse(register.getToolTypes().contains(CorpusImpl.class.getName()));
    assertNull(register.remove(CorpusImpl.class.getName()));
  }

  @Test
  public void putIndexesEverySupportedCategory() throws Exception {
    if(!Gate.isInitialised()) Gate.init();
    CreoleRegisterImpl register = new CreoleRegisterImpl();
    ResourceData lr = new ResourceData();
    lr.setName("LR");
    lr.setClassName(LanguageResource.class.getName());
    lr.setResourceClass(LanguageResource.class);
    ResourceData pr = new ResourceData();
    pr.setName("PR");
    pr.setClassName(ProcessingResource.class.getName());
    pr.setResourceClass(ProcessingResource.class);
    ResourceData vr = new ResourceData();
    vr.setName("VR");
    vr.setClassName(PRViewer.class.getName());
    vr.setResourceClass(PRViewer.class);
    ResourceData controller = new ResourceData();
    controller.setName("Controller");
    controller.setClassName(SerialController.class.getName());
    controller.setResourceClass(SerialController.class);
    ResourceData application = new ResourceData();
    application.setName("Application");
    application.setClassName(PackagedController.class.getName());
    application.setResourceClass(PackagedController.class);

    register.put(lr.getClassName(), lr);
    register.put(pr.getClassName(), pr);
    register.put(vr.getClassName(), vr);
    register.put(controller.getClassName(), controller);
    register.put(application.getClassName(), application);

    assertEquals(1, register.getLrTypes().size());
    assertTrue(register.getLrTypes().contains(LanguageResource.class.getName()));
    assertEquals(2, register.getPrTypes().size());
    assertTrue(register.getPrTypes().contains(ProcessingResource.class.getName()));
    assertEquals(1, register.getVrTypes().size());
    assertTrue(register.getVrTypes().contains(PRViewer.class.getName()));
    assertTrue(register.getControllerTypes().contains(SerialController.class.getName()));
    assertTrue(register.getApplicationTypes().contains(PackagedController.class.getName()));
  }

  @Test
  public void duplicatePutIncrementsReferenceAndRequiresTwoRemovals()
      throws Exception {
    if(!Gate.isInitialised()) Gate.init();
    CreoleRegisterImpl register = new CreoleRegisterImpl();
    ResourceData first = new ResourceData();
    first.setName("first");
    first.setClassName(LanguageResource.class.getName());
    first.setResourceClass(LanguageResource.class);
    ResourceData ignored = new ResourceData();
    ignored.setName("ignored");
    ignored.setClassName(LanguageResource.class.getName());
    ignored.setResourceClass(LanguageResource.class);

    register.put(LanguageResource.class.getName(), first);
    assertSame(first, register.put(LanguageResource.class.getName(), ignored));
    assertSame(first, register.get(LanguageResource.class.getName()));
    assertEquals(2, first.getReferenceCount());

    assertSame(first, register.remove(LanguageResource.class.getName()));
    assertTrue(register.containsKey(LanguageResource.class.getName()));
    assertEquals(1, first.getReferenceCount());
    assertSame(first, register.remove(LanguageResource.class.getName()));
    assertFalse(register.containsKey(LanguageResource.class.getName()));
    assertTrue(register.getLrTypes().isEmpty());
  }

  @Test
  public void clearRemovesMappingsTypesToolsAndLeavesViewsUsable()
      throws Exception {
    if(!Gate.isInitialised()) Gate.init();
    CreoleRegisterImpl register = new CreoleRegisterImpl();
    ResourceData lr = new ResourceData();
    lr.setName("LR");
    lr.setClassName(LanguageResource.class.getName());
    lr.setResourceClass(LanguageResource.class);
    lr.setTool(true);
    ResourceData vr = new ResourceData();
    vr.setName("VR");
    vr.setClassName(VisualResource.class.getName());
    vr.setResourceClass(VisualResource.class);
    register.put(lr.getClassName(), lr);
    register.put(vr.getClassName(), vr);

    register.clear();

    assertEquals(0, register.size());
    assertTrue(register.getLrTypes().isEmpty());
    assertTrue(register.getVrTypes().isEmpty());
    assertTrue(register.getToolTypes().isEmpty());
    assertTrue(register.getPlugins().isEmpty());
  }

  @Test
  public void typedInstanceListsAreLiveTypedAndReadOnly() throws Exception {
    if(!Gate.isInitialised()) Gate.init();
    CreoleRegisterImpl register = new CreoleRegisterImpl();
    ResourceData data = new ResourceData();
    data.setName("Language resource");
    data.setClassName(LanguageResource.class.getName());
    data.setResourceClass(LanguageResource.class);
    LanguageResource first = (LanguageResource)Proxy.newProxyInstance(
        getClass().getClassLoader(), new Class<?>[] {LanguageResource.class},
        (proxy, method, args) -> method.getName().equals("toString") ? "first" : null);
    LanguageResource second = (LanguageResource)Proxy.newProxyInstance(
        getClass().getClassLoader(), new Class<?>[] {LanguageResource.class},
        (proxy, method, args) -> method.getName().equals("toString") ? "second" : null);
    data.addInstantiation(first);
    register.put(data.getClassName(), data);

    List<LanguageResource> view = register.getLrInstances(data.getClassName());
    assertEquals(1, view.size());
    assertSame(first, view.get(0));
    data.addInstantiation(second);
    assertEquals(2, view.size());
    assertSame(second, view.get(0));
    assertEquals(2, register.getLrInstances().size());
    try {
      view.add(first);
      fail("typed view must be read-only");
    } catch(UnsupportedOperationException expected) {
      assertEquals(2, view.size());
    }
    assertTrue(register.getPrInstances("missing.Type").isEmpty());
    assertTrue(register.getVrInstances("missing.Type").isEmpty());
  }

  @Test
  public void typedInstanceListRejectsWrongResourceKindAtAccessTime()
      throws Exception {
    if(!Gate.isInitialised()) Gate.init();
    CreoleRegisterImpl register = new CreoleRegisterImpl();
    ResourceData data = new ResourceData();
    data.setName("mixed metadata");
    data.setClassName(LanguageResource.class.getName());
    data.setResourceClass(LanguageResource.class);
    ProcessingResource wrong = (ProcessingResource)Proxy.newProxyInstance(
        getClass().getClassLoader(), new Class<?>[] {ProcessingResource.class},
        (proxy, method, args) -> null);
    data.addInstantiation(wrong);
    register.put(data.getClassName(), data);

    List<LanguageResource> view = register.getLrInstances(data.getClassName());
    assertEquals(1, view.size());
    try {
      view.get(0);
      fail("the typed list should cast its element");
    } catch(ClassCastException expected) {
      assertTrue(expected.getMessage() == null
          || expected.getMessage().contains("LanguageResource"));
    }
  }

  @Test
  public void publicTypeQueriesExcludePrivateMetadataAndAreImmutable()
      throws Exception {
    if(!Gate.isInitialised()) Gate.init();
    CreoleRegisterImpl register = new CreoleRegisterImpl();
    ResourceData publicLr = new ResourceData();
    publicLr.setName("public LR");
    publicLr.setClassName(LanguageResource.class.getName());
    publicLr.setResourceClass(LanguageResource.class);
    ResourceData privatePr = new ResourceData();
    privatePr.setName("private PR");
    privatePr.setClassName(ProcessingResource.class.getName());
    privatePr.setResourceClass(ProcessingResource.class);
    privatePr.setPrivate(true);
    ResourceData publicVr = new ResourceData();
    publicVr.setName("public VR");
    publicVr.setClassName(VisualResource.class.getName());
    publicVr.setResourceClass(VisualResource.class);
    register.put(publicLr.getClassName(), publicLr);
    register.put(privatePr.getClassName(), privatePr);
    register.put(publicVr.getClassName(), publicVr);

    assertEquals(1, register.getPublicLrTypes().size());
    assertEquals(LanguageResource.class.getName(), register.getPublicLrTypes().get(0));
    assertTrue(register.getPublicPrTypes().isEmpty());
    assertEquals(VisualResource.class.getName(), register.getPublicVrTypes().get(0));
    try {
      register.getPublicLrTypes().clear();
      fail("public type list must be immutable");
    } catch(UnsupportedOperationException expected) {
      assertEquals(1, register.getPublicLrTypes().size());
    }
  }

  @Test
  public void allInstancesIncludesSubtypeMetadataAndFiltersHiddenResources()
      throws Exception {
    if(!Gate.isInitialised()) Gate.init();
    CreoleRegisterImpl register = new CreoleRegisterImpl();
    ResourceData data = new ResourceData();
    data.setName("corpus");
    data.setClassName(CorpusImpl.class.getName());
    data.setResourceClass(CorpusImpl.class);
    CorpusImpl visible = new CorpusImpl();
    visible.setName("visible");
    visible.setFeatures(Factory.newFeatureMap());
    CorpusImpl hidden = new CorpusImpl();
    hidden.setName("hidden");
    hidden.setFeatures(Factory.newFeatureMap());
    Gate.setHiddenAttribute(hidden.getFeatures(), true);
    data.addInstantiation(visible);
    data.addInstantiation(hidden);
    register.put(data.getClassName(), data);

    List<Resource> normal = register.getAllInstances(LanguageResource.class.getName());
    assertEquals(1, normal.size());
    assertSame(visible, normal.get(0));
    List<Resource> withHidden = register.getAllInstances(
        LanguageResource.class.getName(), true);
    assertEquals(2, withHidden.size());
    assertTrue(withHidden.contains(visible));
    assertTrue(withHidden.contains(hidden));
  }

  @Test
  public void allInstancesRejectsUnknownTargetType() throws Exception {
    if(!Gate.isInitialised()) Gate.init();
    CreoleRegisterImpl register = new CreoleRegisterImpl();

    try {
      register.getAllInstances("no.such.ResourceType");
      fail("unknown target type must fail");
    } catch(GateException expected) {
      assertEquals("Invalid type no.such.ResourceType", expected.getMessage());
    }
  }

  @Test
  public void largeAndSmallViewerQueriesMatchDisplayedSupertypeAndMainFirst()
      throws Exception {
    if(!Gate.isInitialised()) Gate.init();
    CreoleRegisterImpl register = new CreoleRegisterImpl();
    ResourceData small = new ResourceData();
    small.setName("small");
    small.setClassName(VisualResource.class.getName());
    small.setResourceClass(VisualResource.class);
    small.setGuiType(ResourceData.SMALL_GUI);
    small.setResourceDisplayed(LanguageResource.class.getName());
    ResourceData secondary = new ResourceData();
    secondary.setName("secondary");
    secondary.setClassName(PRViewer.class.getName());
    secondary.setResourceClass(PRViewer.class);
    secondary.setGuiType(ResourceData.LARGE_GUI);
    secondary.setResourceDisplayed(Resource.class.getName());
    ResourceData main = new ResourceData();
    main.setName("main");
    main.setClassName(AnnotationVisualResource.class.getName());
    main.setResourceClass(AnnotationVisualResource.class);
    main.setGuiType(ResourceData.LARGE_GUI);
    main.setResourceDisplayed(LanguageResource.class.getName());
    main.setIsMainView(true);
    register.put(small.getClassName(), small);
    register.put(secondary.getClassName(), secondary);
    register.put(main.getClassName(), main);

    List<String> large = register.getLargeVRsForResource(CorpusImpl.class.getName());
    assertEquals(2, large.size());
    assertEquals(AnnotationVisualResource.class.getName(), large.get(0));
    assertEquals(PRViewer.class.getName(), large.get(1));
    assertEquals(VisualResource.class.getName(),
        register.getSmallVRsForResource(CorpusImpl.class.getName()).get(0));
    assertTrue(register.getLargeVRsForResource(null).isEmpty());
    try {
      large.remove(0);
      fail("viewer result must be immutable");
    } catch(UnsupportedOperationException expected) {
      assertEquals(2, large.size());
    }
  }

  @Test
  public void viewerQueryRejectsUnknownDisplayedResourceType() throws Exception {
    if(!Gate.isInitialised()) Gate.init();
    CreoleRegisterImpl register = new CreoleRegisterImpl();

    try {
      register.getLargeVRsForResource("not.present.Resource");
      fail("unknown resource class must fail");
    } catch(gate.util.GateRuntimeException expected) {
      assertTrue(expected.getMessage().contains("Couldn't get resource class"));
    }
  }

  @Test
  public void annotationViewerQueriesSeparateGenericAndTypedViewers()
      throws Exception {
    if(!Gate.isInitialised()) Gate.init();
    CreoleRegisterImpl register = new CreoleRegisterImpl();
    ResourceData generic = new ResourceData();
    generic.setName("generic");
    generic.setClassName(AnnotationVisualResource.class.getName());
    generic.setResourceClass(AnnotationVisualResource.class);
    ResourceData typed = new ResourceData();
    typed.setName("typed");
    typed.setClassName("gate.creole.AnnotationVisualResource$typed");
    typed.setResourceClass(AnnotationVisualResource.class);
    typed.setAnnotationTypeDisplayed("Person");
    typed.setIsMainView(true);
    ResourceData nonAnnotation = new ResourceData();
    nonAnnotation.setName("non annotation");
    nonAnnotation.setClassName(VisualResource.class.getName());
    nonAnnotation.setResourceClass(VisualResource.class);
    nonAnnotation.setAnnotationTypeDisplayed("Person");
    register.put(generic.getClassName(), generic);
    register.put(typed.getClassName(), typed);
    register.put(nonAnnotation.getClassName(), nonAnnotation);

    assertEquals(1, register.getAnnotationVRs().size());
    assertEquals(generic.getClassName(), register.getAnnotationVRs().get(0));
    assertEquals(1, register.getAnnotationVRs("Person").size());
    assertEquals(typed.getClassName(), register.getAnnotationVRs("Person").get(0));
    assertTrue(register.getAnnotationVRs("Location").isEmpty());
    assertTrue(register.getAnnotationVRs(null).isEmpty());
    List<String> enabled = register.getVREnabledAnnotationTypes();
    assertEquals(2, enabled.size());
    assertTrue(enabled.contains("Person"));
  }

  @Test
  public void setResourceNameMutatesResourceAndNotifiesListenerOnce()
      throws Exception {
    if(!Gate.isInitialised()) Gate.init();
    CreoleRegisterImpl register = new CreoleRegisterImpl();
    AtomicReference<String> name = new AtomicReference<>("old");
    AtomicInteger renamed = new AtomicInteger();
    AtomicReference<Object[]> callback = new AtomicReference<>();
    Resource resource = (Resource)Proxy.newProxyInstance(getClass().getClassLoader(),
        new Class<?>[] {Resource.class}, (proxy, method, args) -> {
          if(method.getName().equals("getName")) return name.get();
          if(method.getName().equals("setName")) {
            name.set((String)args[0]);
            return null;
          }
          return null;
        });
    CreoleListener listener = (CreoleListener)Proxy.newProxyInstance(
        getClass().getClassLoader(), new Class<?>[] {CreoleListener.class},
        (proxy, method, args) -> {
          if(method.getName().equals("equals")) return proxy == args[0];
          if(method.getName().equals("hashCode")) return System.identityHashCode(proxy);
          if(method.getName().equals("resourceRenamed")) {
            renamed.incrementAndGet();
            callback.set(args);
          }
          return null;
        });
    register.addCreoleListener(listener);
    register.addCreoleListener(listener);

    register.setResourceName(resource, "new");

    assertEquals("new", name.get());
    assertEquals(1, renamed.get());
    assertSame(resource, callback.get()[0]);
    assertEquals("old", callback.get()[1]);
    assertEquals("new", callback.get()[2]);
  }

  @Test
  public void removedCreoleListenerReceivesNoForwardedEvents() throws Exception {
    if(!Gate.isInitialised()) Gate.init();
    CreoleRegisterImpl register = new CreoleRegisterImpl();
    AtomicInteger calls = new AtomicInteger();
    CreoleListener listener = (CreoleListener)Proxy.newProxyInstance(
        getClass().getClassLoader(), new Class<?>[] {CreoleListener.class},
        (proxy, method, args) -> {
          if(method.getName().equals("equals")) return proxy == args[0];
          if(method.getName().equals("hashCode")) return System.identityHashCode(proxy);
          calls.incrementAndGet();
          return null;
        });
    register.addCreoleListener(listener);
    register.removeCreoleListener(listener);

    register.resourceLoaded(null);
    register.resourceUnloaded(null);
    register.resourceRenamed(null, "a", "b");
    register.datastoreOpened(null);
    register.datastoreCreated(null);
    register.datastoreClosed(null);

    assertEquals(0, calls.get());
  }

  @Test
  public void eventForwardingInvokesEveryCreoleCallback() throws Exception {
    if(!Gate.isInitialised()) Gate.init();
    CreoleRegisterImpl register = new CreoleRegisterImpl();
    AtomicInteger loaded = new AtomicInteger();
    AtomicInteger unloaded = new AtomicInteger();
    AtomicInteger renamed = new AtomicInteger();
    AtomicInteger opened = new AtomicInteger();
    AtomicInteger created = new AtomicInteger();
    AtomicInteger closed = new AtomicInteger();
    CreoleListener listener = (CreoleListener)Proxy.newProxyInstance(
        getClass().getClassLoader(), new Class<?>[] {CreoleListener.class},
        (proxy, method, args) -> {
          if(method.getName().equals("resourceLoaded")) loaded.incrementAndGet();
          if(method.getName().equals("resourceUnloaded")) unloaded.incrementAndGet();
          if(method.getName().equals("resourceRenamed")) renamed.incrementAndGet();
          if(method.getName().equals("datastoreOpened")) opened.incrementAndGet();
          if(method.getName().equals("datastoreCreated")) created.incrementAndGet();
          if(method.getName().equals("datastoreClosed")) closed.incrementAndGet();
          return null;
        });
    register.addCreoleListener(listener);

    register.resourceLoaded(null);
    register.resourceUnloaded(null);
    register.resourceRenamed(null, "old", "new");
    register.datastoreOpened(null);
    register.datastoreCreated(null);
    register.datastoreClosed(null);

    assertEquals(1, loaded.get());
    assertEquals(1, unloaded.get());
    assertEquals(1, renamed.get());
    assertEquals(1, opened.get());
    assertEquals(1, created.get());
    assertEquals(1, closed.get());
  }

  @Test
  public void pluginListenerCanBeAddedAndRemovedWithoutSideEffects()
      throws Exception {
    if(!Gate.isInitialised()) Gate.init();
    CreoleRegisterImpl register = new CreoleRegisterImpl();
    AtomicInteger calls = new AtomicInteger();
    PluginListener listener = (PluginListener)Proxy.newProxyInstance(
        getClass().getClassLoader(), new Class<?>[] {PluginListener.class},
        (proxy, method, args) -> {
          if(method.getName().equals("equals")) return proxy == args[0];
          if(method.getName().equals("hashCode")) return System.identityHashCode(proxy);
          calls.incrementAndGet();
          return null;
        });

    register.addPluginListener(listener);
    register.removePluginListener(listener);
    register.removeDirectory(null);

    assertEquals(0, calls.get());
    assertTrue(register.getPlugins().isEmpty());
  }
}

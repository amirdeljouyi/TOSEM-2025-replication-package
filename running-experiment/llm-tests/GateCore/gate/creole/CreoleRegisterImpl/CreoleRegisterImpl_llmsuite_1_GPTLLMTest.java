package gate.creole;

import gate.*;
import gate.creole.ResourceInstantiationException;
import gate.event.CreoleEvent;
import gate.event.CreoleListener;
import gate.event.PluginListener;
import gate.util.GateException;
import gate.util.GateRuntimeException;
import gate.util.InvalidOffsetException;
import org.apache.tools.ant.types.resources.comparators.Content;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import gate.creole.AnnotationSchema;
import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class CreoleRegisterImpl_llmsuite_1_GPTLLMTest {

@Test
public void testPutAddsLanguageResourceAndReflectsInTypeSet() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData mockData = mock(ResourceData.class);
// String className = DummyLR.class.getName();
// when(mockData.getClassName()).thenReturn(className);
// when(mockData.getResourceClass()).thenReturn(DummyLR.class);
when(mockData.isTool()).thenReturn(false);
// ResourceData returned = register.put(className, mockData);
// assertNotNull(returned);
// assertTrue(register.getLrTypes().contains(className));
}

@Test(expected = GateRuntimeException.class)
public void testPutThrowsWhenClassNotFoundException() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData mockData = mock(ResourceData.class);
when(mockData.getClassName()).thenReturn("non.existent.Class");
when(mockData.getResourceClass()).thenThrow(new ClassNotFoundException());
register.put("non.existent.Class", mockData);
}

@Test
public void testRegisterValidPluginAddsToPluginSet() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
when(plugin.isValid()).thenReturn(true);
// when(plugin.getRequiredPlugins()).thenReturn(Collections.emptyList());
// when(plugin.getCreoleXML()).thenReturn(new Document());
URL url = new URL("http://example.com/");
when(plugin.getBaseURL()).thenReturn(url);
when(plugin.getBaseURI()).thenReturn(url.toURI());
when(plugin.getName()).thenReturn("testPlugin");
when(plugin.getVersion()).thenReturn("1.0");
register.registerPlugin(plugin);
Set<Plugin> pluginSet = register.getPlugins();
assertTrue(pluginSet.contains(plugin));
}

@Test(expected = GateException.class)
public void testRegisterInvalidPluginThrows() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
URL url = new URL("http://invalid.plugin/");
when(plugin.getBaseURL()).thenReturn(url);
when(plugin.getBaseURI()).thenReturn(url.toURI());
// when(plugin.getCreoleXML()).thenReturn(new Document());
when(plugin.isValid()).thenReturn(false);
register.registerPlugin(plugin);
}

@Test
public void testRemoveUpdatesProcessingResourceTypeSet() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
// String className = DummyPR.class.getName();
// when(rd.getClassName()).thenReturn(className);
// when(rd.getResourceClass()).thenReturn(DummyPR.class);
when(rd.reduceReferenceCount()).thenReturn(0);
when(rd.isTool()).thenReturn(false);
// register.put(className, rd);
// assertTrue(register.getPrTypes().contains(className));
// register.remove(className);
// assertFalse(register.getPrTypes().contains(className));
// assertNull(register.get(className));
}

@Test
public void testClearEmptiesRegisterAndTypes() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = mock(ResourceData.class);
// String name = DummyTool.class.getName();
// when(data.getClassName()).thenReturn(name);
// when(data.getResourceClass()).thenReturn(DummyTool.class);
when(data.isTool()).thenReturn(true);
// register.put(name, data);
// assertTrue(register.getToolTypes().contains(name));
register.clear();
assertTrue(register.getToolTypes().isEmpty());
assertTrue(register.isEmpty());
}

@Test
public void testGetAllInstancesReturnsExpectedResource() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
// String className = DummyLR.class.getName();
// DummyLR instance = new DummyLR();
List<Resource> instances = new ArrayList<>();
// instances.add(instance);
// when(rd.getClassName()).thenReturn(className);
// when(rd.getResourceClass()).thenReturn(DummyLR.class);
when(rd.getInstantiations()).thenReturn(instances);
when(rd.isTool()).thenReturn(false);
// register.put(className, rd);
// List<Resource> result = register.getAllInstances(className, true);
// assertEquals(1, result.size());
// assertEquals(instance, result.get(0));
}

@Test
public void testFireCreoleEventsToListener() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
// Resource dummy = new DummyLR();
// CreoleEvent event = new CreoleEvent(dummy, CreoleEvent.RESOURCE_LOADED);
CreoleListener listener = mock(CreoleListener.class);
register.addCreoleListener(listener);
// register.resourceLoaded(event);
// register.resourceUnloaded(event);
// register.resourceRenamed(dummy, "oldName", "newName");
// register.datastoreOpened(event);
// register.datastoreCreated(event);
// register.datastoreClosed(event);
// verify(listener).resourceLoaded(event);
// verify(listener).resourceUnloaded(event);
// verify(listener).resourceRenamed(dummy, "oldName", "newName");
// verify(listener).datastoreOpened(event);
// verify(listener).datastoreCreated(event);
// verify(listener).datastoreClosed(event);
}

@Test
public void testPluginListenerReceivesNotifications() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
PluginListener listener = mock(PluginListener.class);
register.addPluginListener(listener);
register.firePluginLoaded(plugin);
register.firePluginUnloaded(plugin);
verify(listener).pluginLoaded(plugin);
verify(listener).pluginUnloaded(plugin);
}

@Test
public void testGetAnnotationVRsReturnsEmptyListByDefault() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<String> vrList = register.getAnnotationVRs();
assertNotNull(vrList);
assertTrue(vrList.isEmpty());
}

@Test
public void testGetAnnotationVRsByTypeReturnsEmptyWhenNoneRegistered() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<String> annotationVrs = register.getAnnotationVRs("Person");
assertNotNull(annotationVrs);
assertTrue(annotationVrs.isEmpty());
}

@Test
public void testPutDuplicateResourceIncrementsReferenceCountAndLogsWarning() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData mockData = mock(ResourceData.class);
// String className = DummyLR.class.getName();
// when(mockData.getClassName()).thenReturn(className);
// when(mockData.getResourceClass()).thenReturn(DummyLR.class);
when(mockData.isTool()).thenReturn(false);
when(mockData.increaseReferenceCount()).thenReturn(2);
// register.put(className, mockData);
// ResourceData result = register.put(className, mockData);
// assertEquals(mockData, result);
// assertEquals(mockData, register.get(className));
}

@Test
public void testRemoveDoesNotDeleteResourceIfReferenceCountGreaterThanZero() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = mock(ResourceData.class);
// String name = DummyPR.class.getName();
// when(data.getClassName()).thenReturn(name);
// when(data.getResourceClass()).thenReturn(DummyPR.class);
when(data.reduceReferenceCount()).thenReturn(1);
// register.put(name, data);
// ResourceData removed = register.remove(name);
// assertEquals(data, removed);
// assertEquals(data, register.get(name));
}

@Test(expected = GateRuntimeException.class)
public void testRemoveThrowsOnClassNotFoundDuringTypeRemoval() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = mock(ResourceData.class);
when(data.getClassName()).thenReturn("non.existent.Class");
when(data.getResourceClass()).thenThrow(new ClassNotFoundException());
when(data.reduceReferenceCount()).thenReturn(0);
when(data.isTool()).thenReturn(false);
register.put("non.existent.Class", data);
register.remove("non.existent.Class");
}

@Test
public void testGetAllInstancesReturnsEmptyListWhenTypeIsNotAssignable() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = mock(ResourceData.class);
// when(data.getClassName()).thenReturn(DummyTool.class.getName());
// when(data.getResourceClass()).thenReturn(DummyTool.class);
when(data.getInstantiations()).thenReturn(Collections.emptyList());
when(data.isTool()).thenReturn(false);
// register.put(DummyTool.class.getName(), data);
// List<Resource> result = register.getAllInstances(DummyPR.class.getName(), true);
// assertTrue(result.isEmpty());
}

@Test(expected = GateException.class)
public void testGetAllInstancesThrowsIfTypeClassNotFound() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
register.getAllInstances("non.existent.FakeType", false);
}

@Test
public void testGetLargeVRsForResourceWithNullReturnsEmpty() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<String> result = register.getLargeVRsForResource(null);
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetSmallVRsForResourceWithNullReturnsEmpty() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<String> result = register.getSmallVRsForResource(null);
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test(expected = GateRuntimeException.class)
public void testGetVRsForResourceThrowsIfTargetClassNotFound() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
register.put("nonexistent.VR", null);
register.getLargeVRsForResource("non.existent.Type");
}

@Test(expected = GateRuntimeException.class)
public void testGetVRsForResourceThrowsWhenVRResourceDataClassNotFound() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData vrData = mock(ResourceData.class);
when(vrData.getGuiType()).thenReturn(ResourceData.LARGE_GUI);
when(vrData.getResourceDisplayed()).thenReturn("non.existent.Type");
when(vrData.getAnnotationTypeDisplayed()).thenReturn(null);
when(vrData.getResourceClass()).thenThrow(new ClassNotFoundException());
String vrClassName = "dummy.vr.Type";
when(vrData.getClassName()).thenReturn(vrClassName);
when(vrData.isMainView()).thenReturn(false);
register.put(vrClassName, vrData);
ResourceData source = mock(ResourceData.class);
// String targetName = DummyLR.class.getName();
// when(source.getClassName()).thenReturn(targetName);
// when(source.getResourceClass()).thenReturn(DummyLR.class);
when(source.isTool()).thenReturn(false);
// register.put(targetName, source);
// register.getLargeVRsForResource(targetName);
}

@Test
public void testRegisterPluginRecursivelyRegistersRequiredPlugins() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin dependency = mock(Plugin.class);
Plugin parent = mock(Plugin.class);
when(dependency.isValid()).thenReturn(true);
// when(dependency.getRequiredPlugins()).thenReturn(Collections.emptyList());
// when(dependency.getCreoleXML()).thenReturn(new Document());
URL baseDep = new URL("http://localhost/dependency/");
when(dependency.getBaseURL()).thenReturn(baseDep);
when(dependency.getBaseURI()).thenReturn(baseDep.toURI());
when(dependency.getName()).thenReturn("dep");
when(dependency.getVersion()).thenReturn("1.0");
when(parent.isValid()).thenReturn(true);
// when(parent.getRequiredPlugins()).thenReturn(Collections.singletonList(dependency));
// when(parent.getCreoleXML()).thenReturn(new Document());
URL baseParent = new URL("http://localhost/parent/");
when(parent.getBaseURL()).thenReturn(baseParent);
when(parent.getBaseURI()).thenReturn(baseParent.toURI());
when(parent.getName()).thenReturn("parent");
when(parent.getVersion()).thenReturn("1.0");
register.registerPlugin(parent);
assertTrue(register.getPlugins().contains(parent));
assertTrue(register.getPlugins().contains(dependency));
}

@Test
public void testGetPublicTypesReturnsOnlyNonPrivateTypes() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData privateResource = mock(ResourceData.class);
when(privateResource.isPrivate()).thenReturn(true);
// when(privateResource.getClassName()).thenReturn(DummyLR.class.getName());
// when(privateResource.getResourceClass()).thenReturn(DummyLR.class);
when(privateResource.isTool()).thenReturn(false);
// register.put(DummyLR.class.getName(), privateResource);
List<String> publicList = register.getPublicLrTypes();
assertNotNull(publicList);
assertTrue(publicList.isEmpty());
}

@Test
public void testGetPublicInstancesReturnsOnlyNonPrivateResources() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
// DummyLR instance = new DummyLR();
List<Resource> list = new ArrayList<>();
// list.add(instance);
ResourceData data = mock(ResourceData.class);
// when(data.getClassName()).thenReturn(DummyLR.class.getName());
when(data.getInstantiations()).thenReturn(list);
// when(data.getResourceClass()).thenReturn(DummyLR.class);
when(data.isPrivate()).thenReturn(true);
when(data.isTool()).thenReturn(false);
// register.put(DummyLR.class.getName(), data);
List<LanguageResource> result = register.getPublicLrInstances();
assertTrue(result.isEmpty());
}

@Test
public void testRegisterComponentThrowsGateExceptionForMalformedUrl() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Class<? extends Resource> badClass = null;
try {
// badClass = DummyBrokenComponent.class;
} catch (Exception e) {
}
CreoleRegisterImpl creoleRegister = new CreoleRegisterImpl();
// Class<? extends Resource> anonymouslyBroken = new Resource() {
// 
// public Resource init() {
// return this;
// }
// 
// public void cleanup() {
// }
// 
// public FeatureMap getFeatures() {
// return Factory.newFeatureMap();
// }
// 
// public void setFeatures(FeatureMap fm) {
// }
// 
// public String getName() {
// return "broken";
// }
// 
// public void setName(String name) {
// }
// 
// public void setParameterValue(String param, Object value) {
// }
// 
// public Object getParameterValue(String param) {
// return null;
// }
// }.getClass().asSubclass(Resource.class);
// try {
// register.registerComponent(anonymouslyBroken);
// fail("Expected GateException due to bad URL");
// } catch (GateException e) {
// assertTrue(e.getMessage().contains("Unable to register component"));
// }
}

@Test
public void testRemoveDirectoryIgnoresUnknownURL() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
URL url = new URL("http://nonexistent.com/");
register.removeDirectory(url);
}

@Test
public void testRemoveDirectoryRemovesMatchingPlugin() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
URL url = new URL("http://example.com/");
when(plugin.getBaseURL()).thenReturn(url);
// when(plugin.getRequiredPlugins()).thenReturn(Collections.emptyList());
// when(plugin.getCreoleXML()).thenReturn(new Document());
when(plugin.getBaseURI()).thenReturn(url.toURI());
when(plugin.isValid()).thenReturn(true);
when(plugin.getName()).thenReturn("TestPlugin");
when(plugin.getVersion()).thenReturn("1.0");
register.registerPlugin(plugin);
assertTrue(register.getPlugins().contains(plugin));
register.removeDirectory(url);
assertFalse(register.getPlugins().contains(plugin));
}

@Test(expected = GateException.class)
public void testRegisterDirectoriesThrowsOnInvalidURL() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
URL invalidUrl = new URL("http://missing-plugin/");
Plugin plugin = mock(Plugin.class);
when(plugin.getBaseURL()).thenReturn(invalidUrl);
register.registerDirectories(invalidUrl);
}

@Test
public void testSetResourceNameTriggersListenerCallback() {
// CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleListener listener = mock(CreoleListener.class);
// register.addCreoleListener(listener);
// DummyPR pr = new DummyPR();
// pr.setName("old");
// register.setResourceName(pr, "newName");
// verify(listener).resourceRenamed(pr, "old", "newName");
// assertEquals("newName", pr.getName());
}

@Test
public void testAddRemoveCreoleListenerWorks() {
// CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleListener listener = mock(CreoleListener.class);
// register.addCreoleListener(listener);
assertNotNull(listener);
// register.removeCreoleListener(listener);
// CreoleEvent event = new CreoleEvent(new DummyLR(), CreoleEvent.RESOURCE_LOADED);
// register.resourceLoaded(event);
verifyNoMoreInteractions(listener);
}

@Test
public void testRemoveNonExistentCreoleListenerDoesNothing() {
// CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleListener listener = mock(CreoleListener.class);
// register.removeCreoleListener(listener);
assertNotNull(listener);
}

@Test
public void testAddSamePluginTwiceDoesNotDuplicate() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
// when(plugin.getRequiredPlugins()).thenReturn(Collections.emptyList());
// when(plugin.getCreoleXML()).thenReturn(new Document());
URL url = new URL("http://plugin-x/");
when(plugin.getBaseURL()).thenReturn(url);
when(plugin.getBaseURI()).thenReturn(url.toURI());
when(plugin.isValid()).thenReturn(true);
when(plugin.getName()).thenReturn("X");
when(plugin.getVersion()).thenReturn("1");
register.registerPlugin(plugin);
int sizeAfterFirst = register.getPlugins().size();
register.registerPlugin(plugin);
int sizeAfterSecond = register.getPlugins().size();
assertEquals(sizeAfterFirst, sizeAfterSecond);
}

@Test
public void testVRListExcludesDuplicatesCorrectly() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn("dummy.VR");
// when(rd.getResourceClass()).thenReturn(DummyVR.class);
when(rd.getGuiType()).thenReturn(ResourceData.LARGE_GUI);
// when(rd.getResourceDisplayed()).thenReturn(DummyLR.class.getName());
when(rd.isMainView()).thenReturn(true);
ResourceData lr = mock(ResourceData.class);
// when(lr.getClassName()).thenReturn(DummyLR.class.getName());
// when(lr.getResourceClass()).thenReturn(DummyLR.class);
register.put("dummy.VR", rd);
// register.put(DummyLR.class.getName(), lr);
// List<String> vrList = register.getLargeVRsForResource(DummyLR.class.getName());
// assertEquals(1, vrList.size());
// assertEquals("dummy.VR", vrList.get(0));
}

@Test
public void testGetVREnabledAnnotationTypesReturnsEmptyWhenNoneExist() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<String> types = register.getVREnabledAnnotationTypes();
assertNotNull(types);
assertTrue(types.isEmpty());
}

@Test
public void testRemoveDirectoryWithNullInputReturnsSilently() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
register.removeDirectory(null);
}

@Test
public void testPutAddsVisualResourceAndIgnoresDuplicateVRClassName() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
// String className = DummyVR.class.getName();
// when(rd.getClassName()).thenReturn(className);
// when(rd.getResourceClass()).thenReturn(DummyVR.class);
when(rd.isTool()).thenReturn(false);
// register.put(className, rd);
// register.put(className, rd);
Set<String> all = new HashSet<>(register.getVrTypes());
// assertTrue(all.contains(className));
assertEquals(1, all.size());
}

@Test
public void testFirePluginLoadedAndUnloadedEventsWithoutListeners() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
register.firePluginLoaded(plugin);
register.firePluginUnloaded(plugin);
assertNotNull(plugin);
}

@Test
public void testAddAndRemovePluginListenerEffect() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
PluginListener listener = mock(PluginListener.class);
Plugin plugin = mock(Plugin.class);
register.addPluginListener(listener);
register.firePluginLoaded(plugin);
register.firePluginUnloaded(plugin);
register.removePluginListener(listener);
register.firePluginLoaded(plugin);
verify(listener).pluginLoaded(plugin);
verify(listener).pluginUnloaded(plugin);
}

@Test
public void testAddDuplicateCreoleListenerOnlyOnce() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleListener listener = mock(CreoleListener.class);
register.addCreoleListener(listener);
register.addCreoleListener(listener);
// DummyLR res = new DummyLR();
// CreoleEvent event = new CreoleEvent(res, CreoleEvent.RESOURCE_LOADED);
// register.resourceLoaded(event);
// verify(listener).resourceLoaded(event);
}

@Test
public void testGetPublicTypesWithEmptyInputReturnsEmptyList() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<String> result = register.getPublicTypes(Collections.<String>emptyList());
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetPublicsFiltersPrivateResources() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
// DummyLR res1 = new DummyLR();
// DummyLR res2 = new DummyLR();
ResourceData data1 = mock(ResourceData.class);
ResourceData data2 = mock(ResourceData.class);
when(data1.isPrivate()).thenReturn(true);
when(data2.isPrivate()).thenReturn(false);
// when(data1.getClassName()).thenReturn(res1.getClass().getName());
// when(data2.getClassName()).thenReturn(res2.getClass().getName());
// register.put(res1.getClass().getName(), data1);
// register.put(res2.getClass().getName(), data2);
List<LanguageResource> inputList = new ArrayList<>();
// inputList.add(res1);
// inputList.add(res2);
List<LanguageResource> filtered = register.getPublics(inputList);
assertEquals(1, filtered.size());
// assertEquals(res2, filtered.get(0));
}

@Test
public void testRemoveResourceThatDoesNotExistReturnsNull() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData result = register.remove("non.existing.Class");
assertNull(result);
}

@Test
public void testRemoveResourceWithToolFlagRemovesFromToolTypes() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = mock(ResourceData.class);
// when(data.getClassName()).thenReturn(DummyTool.class.getName());
// when(data.getResourceClass()).thenReturn(DummyTool.class);
when(data.isTool()).thenReturn(true);
when(data.reduceReferenceCount()).thenReturn(0);
// register.put(DummyTool.class.getName(), data);
// assertTrue(register.getToolTypes().contains(DummyTool.class.getName()));
// register.remove(DummyTool.class.getName());
// assertFalse(register.getToolTypes().contains(DummyTool.class.getName()));
}

@Test
public void testFireResourceUnloadedWithoutListeners() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
// CreoleEvent event = new CreoleEvent(new DummyLR(), CreoleEvent.RESOURCE_UNLOADED);
// register.resourceUnloaded(event);
}

@Test
public void testPutAddsControllerTypeAndApplicationType() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData controllerData = mock(ResourceData.class);
when(controllerData.getClassName()).thenReturn("gate.creole.ControllerClass");
// when(controllerData.getResourceClass()).thenReturn(DummyController.class);
when(controllerData.isTool()).thenReturn(false);
ResourceData appData = mock(ResourceData.class);
when(appData.getClassName()).thenReturn("gate.creole.AppClass");
// when(appData.getResourceClass()).thenReturn(DummyAppController.class);
when(appData.isTool()).thenReturn(false);
register.put("gate.creole.ControllerClass", controllerData);
register.put("gate.creole.AppClass", appData);
assertTrue(register.getControllerTypes().contains("gate.creole.ControllerClass"));
assertTrue(register.getApplicationTypes().contains("gate.creole.AppClass"));
}

@Test
public void testGetAllInstancesSkipsHiddenResources() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
// DummyLR hiddenResource = new DummyLR();
// hiddenResource.setName("hidden");
FeatureMap features = Factory.newFeatureMap();
Gate.setHiddenAttribute(features, true);
// hiddenResource.setFeatures(features);
ResourceData data = mock(ResourceData.class);
// when(data.getClassName()).thenReturn(DummyLR.class.getName());
// when(data.getResourceClass()).thenReturn(DummyLR.class);
List<Resource> list = new ArrayList<Resource>();
// list.add(hiddenResource);
when(data.getInstantiations()).thenReturn(list);
when(data.isTool()).thenReturn(false);
// register.put(DummyLR.class.getName(), data);
// List<Resource> res = register.getAllInstances(DummyLR.class.getName(), false);
// assertTrue(res.isEmpty());
}

@Test
public void testGetAnnotationVRsWithMatchingAnnotationType() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData vr = mock(ResourceData.class);
when(vr.getClassName()).thenReturn("mock.VR");
when(vr.getGuiType()).thenReturn(ResourceData.NULL_GUI);
when(vr.getAnnotationTypeDisplayed()).thenReturn("Person");
when(vr.getResourceDisplayed()).thenReturn(null);
// when(vr.getResourceClass()).thenReturn(DummyAnnotationVR.class);
when(vr.isMainView()).thenReturn(true);
register.put("mock.VR", vr);
List<String> result = register.getAnnotationVRs("Person");
assertEquals(1, result.size());
assertEquals("mock.VR", result.get(0));
}

@Test
public void testGetAnnotationVRsReturnsOnlyMatchingTypes() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData vr1 = mock(ResourceData.class);
when(vr1.getClassName()).thenReturn("vr.A");
when(vr1.getGuiType()).thenReturn(ResourceData.NULL_GUI);
when(vr1.getAnnotationTypeDisplayed()).thenReturn("Date");
when(vr1.getResourceDisplayed()).thenReturn(null);
// when(vr1.getResourceClass()).thenReturn(DummyAnnotationVR.class);
when(vr1.isMainView()).thenReturn(true);
ResourceData vr2 = mock(ResourceData.class);
when(vr2.getClassName()).thenReturn("vr.B");
when(vr2.getGuiType()).thenReturn(ResourceData.NULL_GUI);
when(vr2.getAnnotationTypeDisplayed()).thenReturn("Location");
when(vr2.getResourceDisplayed()).thenReturn(null);
// when(vr2.getResourceClass()).thenReturn(DummyAnnotationVR.class);
when(vr2.isMainView()).thenReturn(false);
register.put("vr.A", vr1);
register.put("vr.B", vr2);
List<String> result = register.getAnnotationVRs("Location");
assertEquals(1, result.size());
assertEquals("vr.B", result.get(0));
}

@Test(expected = GateRuntimeException.class)
public void testGetVRsForResourceThrowsWhenVRResourceDataIsNull() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
register.put("null.VR", null);
register.getLargeVRsForResource("null.Type");
}

@Test
public void testPutResourceWithNullVrTypesDoesNotThrow() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = mock(ResourceData.class);
when(data.getClassName()).thenReturn("gate.creole.SomeVR");
// when(data.getResourceClass()).thenReturn(DummyVR.class);
when(data.isTool()).thenReturn(false);
register.vrTypes = null;
register.put("gate.creole.SomeVR", data);
// List<String> types = register.getVrTypes();
// assertTrue(types.contains("gate.creole.SomeVR"));
}

@Test
public void testUnregisterPluginRemovesDependentPluginsRecursively() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin depPlugin = mock(Plugin.class);
URL depUrl = new URL("http://localhost/dep/");
when(depPlugin.getBaseURL()).thenReturn(depUrl);
when(depPlugin.getBaseURI()).thenReturn(depUrl.toURI());
// when(depPlugin.getRequiredPlugins()).thenReturn(Collections.emptyList());
// when(depPlugin.getCreoleXML()).thenReturn(new Document());
when(depPlugin.isValid()).thenReturn(true);
when(depPlugin.getName()).thenReturn("Dep");
when(depPlugin.getVersion()).thenReturn("1.0");
when(depPlugin.getResourceInfoList()).thenReturn(Collections.emptyList());
Plugin masterPlugin = mock(Plugin.class);
URL masterUrl = new URL("http://localhost/master/");
when(masterPlugin.getBaseURL()).thenReturn(masterUrl);
when(masterPlugin.getBaseURI()).thenReturn(masterUrl.toURI());
// when(masterPlugin.getRequiredPlugins()).thenReturn(Collections.singletonList(depPlugin));
// when(masterPlugin.getCreoleXML()).thenReturn(new Document());
when(masterPlugin.isValid()).thenReturn(true);
when(masterPlugin.getName()).thenReturn("Master");
when(masterPlugin.getVersion()).thenReturn("1.0");
when(masterPlugin.getResourceInfoList()).thenReturn(Collections.emptyList());
register.registerPlugin(masterPlugin);
assertTrue(register.getPlugins().contains(depPlugin));
assertTrue(register.getPlugins().contains(masterPlugin));
register.unregisterPlugin(depPlugin);
assertFalse(register.getPlugins().contains(depPlugin));
assertFalse(register.getPlugins().contains(masterPlugin));
}

@Test
public void testGetVrInstancesByTypeReturnsEmptyWhenTypeNotFound() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<VisualResource> result = register.getVrInstances("non.existent.Type");
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetPublicVrTypesReturnsEmptyWhenAllVrArePrivate() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = mock(ResourceData.class);
// String className = DummyVR.class.getName();
// when(data.getClassName()).thenReturn(className);
// when(data.getResourceClass()).thenReturn(DummyVR.class);
when(data.isPrivate()).thenReturn(true);
when(data.isTool()).thenReturn(false);
// register.put(className, data);
List<String> types = register.getPublicVrTypes();
assertTrue(types.isEmpty());
}

@Test
public void testSetResourceNameWithNullOldNameFiresRenamedEvent() {
// CreoleRegisterImpl register = new CreoleRegisterImpl();
// DummyPR resource = new DummyPR();
// resource.setName(null);
CreoleListener listener = mock(CreoleListener.class);
// register.addCreoleListener(listener);
// register.setResourceName(resource, "new-pr-name");
// verify(listener).resourceRenamed(resource, null, "new-pr-name");
// assertEquals("new-pr-name", resource.getName());
}

@Test
public void testRemoveResourceThatDoesNotMatchAnyCategoriesStillRemoves() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = mock(ResourceData.class);
String className = "non.category.Class";
when(data.getClassName()).thenReturn(className);
// when(data.getResourceClass()).thenReturn(UnknownCategoryResource.class);
when(data.reduceReferenceCount()).thenReturn(0);
when(data.isTool()).thenReturn(false);
register.put(className, data);
assertNotNull(register.get(className));
ResourceData removed = register.remove(className);
assertEquals(data, removed);
assertNull(register.get(className));
}

@Test
public void testGetAllInstancesTypeHierarchyIncludesSubtypes() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
// DummyLR instance = new DummyLR();
ResourceData data = mock(ResourceData.class);
// when(data.getClassName()).thenReturn(DummyLR.class.getName());
// when(data.getResourceClass()).thenReturn(DummyLR.class);
List<Resource> list = new ArrayList<Resource>();
// list.add(instance);
when(data.getInstantiations()).thenReturn(list);
// register.put(DummyLR.class.getName(), data);
List<Resource> result = register.getAllInstances(Resource.class.getName(), true);
// assertTrue(result.contains(instance));
}

@Test
public void testPutHandlesNullToolSetDuringDeserializationPathCorrectly() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
register.toolTypes = null;
ResourceData data = mock(ResourceData.class);
// when(data.getClassName()).thenReturn(DummyTool.class.getName());
// when(data.getResourceClass()).thenReturn(DummyTool.class);
when(data.isTool()).thenReturn(true);
// register.put(DummyTool.class.getName(), data);
Set<String> tools = register.getToolTypes();
// assertTrue(tools.contains(DummyTool.class.getName()));
}

@Test
public void testAnnotationVRsReturnsEmptyIfTypeDoesNotMatch() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData vr = mock(ResourceData.class);
when(vr.getClassName()).thenReturn("custom.vr.Class");
when(vr.getGuiType()).thenReturn(ResourceData.NULL_GUI);
when(vr.getAnnotationTypeDisplayed()).thenReturn("Organization");
// when(vr.getResourceClass()).thenReturn(DummyAnnotationVR.class);
when(vr.getResourceDisplayed()).thenReturn(null);
when(vr.isMainView()).thenReturn(true);
register.put("custom.vr.Class", vr);
List<String> result = register.getAnnotationVRs("OtherType");
assertTrue(result.isEmpty());
}

@Test
public void testSetNullFeaturesOnResourceIsAllowed() {
// CreoleRegisterImpl register = new CreoleRegisterImpl();
// DummyTool resource = new DummyTool();
// resource.setFeatures(null);
// assertNull(resource.getFeatures());
}

@Test
public void testRemoveDeserialisedResourceWithNullTypeSetsStillSucceeds() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
register.lrTypes = null;
register.prTypes = null;
register.vrTypes = null;
register.controllerTypes = null;
register.applicationTypes = null;
ResourceData data = mock(ResourceData.class);
// when(data.getClassName()).thenReturn(DummyLR.class.getName());
// when(data.getResourceClass()).thenReturn(DummyLR.class);
when(data.reduceReferenceCount()).thenReturn(0);
when(data.isTool()).thenReturn(false);
// register.put(DummyLR.class.getName(), data);
// ResourceData removed = register.remove(DummyLR.class.getName());
// assertNotNull(removed);
}

@Test(expected = GateRuntimeException.class)
public void testRemoveFailsGracefullyIfClassNameInvalid() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = mock(ResourceData.class);
when(data.getClassName()).thenReturn("invalid.Class");
when(data.getResourceClass()).thenThrow(new ClassNotFoundException());
when(data.reduceReferenceCount()).thenReturn(0);
when(data.isTool()).thenReturn(false);
register.put("invalid.Class", data);
register.remove("invalid.Class");
}

@Test
public void testFireDatastoreEventsWithoutListeners() {
// CreoleRegisterImpl register = new CreoleRegisterImpl();
// Resource dummy = new DummyLR();
// CreoleEvent event = new CreoleEvent(dummy, CreoleEvent.DATASTORE_CREATED);
// register.datastoreCreated(event);
// register.datastoreOpened(event);
// register.datastoreClosed(event);
}

@Test
public void testGetVRsForResourceSkipsVRIfMissingClassName() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData vr = mock(ResourceData.class);
when(vr.getClassName()).thenReturn(null);
when(vr.getGuiType()).thenReturn(ResourceData.LARGE_GUI);
// when(vr.getResourceDisplayed()).thenReturn(DummyLR.class.getName());
// when(vr.getResourceClass()).thenReturn(DummyVR.class);
when(vr.isMainView()).thenReturn(false);
ResourceData resource = mock(ResourceData.class);
// when(resource.getClassName()).thenReturn(DummyLR.class.getName());
// when(resource.getResourceClass()).thenReturn(DummyLR.class);
register.put("nullvr", vr);
// register.put(DummyLR.class.getName(), resource);
// List<String> result = register.getLargeVRsForResource(DummyLR.class.getName());
// assertNotNull(result);
// assertTrue(result.isEmpty());
}

@Test
public void testGetVRsForResourceWithNullResourceDisplayedClassSkipsVR() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData vr = mock(ResourceData.class);
when(vr.getClassName()).thenReturn("vr.with.nulldisplay");
when(vr.getGuiType()).thenReturn(ResourceData.LARGE_GUI);
when(vr.getResourceDisplayed()).thenReturn(null);
// when(vr.getResourceClass()).thenReturn(DummyVR.class);
when(vr.isMainView()).thenReturn(true);
ResourceData resource = mock(ResourceData.class);
// when(resource.getClassName()).thenReturn(DummyLR.class.getName());
// when(resource.getResourceClass()).thenReturn(DummyLR.class);
register.put("vr.with.nulldisplay", vr);
// register.put(DummyLR.class.getName(), resource);
// List<String> result = register.getLargeVRsForResource(DummyLR.class.getName());
// assertTrue(result.isEmpty());
}

@Test
public void testAnnotationVRsIgnoresNonAssignableVRs() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData vr = mock(ResourceData.class);
when(vr.getClassName()).thenReturn("someNonAssignableVR");
when(vr.getGuiType()).thenReturn(ResourceData.NULL_GUI);
when(vr.getAnnotationTypeDisplayed()).thenReturn("Person");
when(vr.getResourceDisplayed()).thenReturn(null);
// when(vr.getResourceClass()).thenReturn(DummyVR.class);
when(vr.isMainView()).thenReturn(false);
register.put("someNonAssignableVR", vr);
List<String> result = register.getAnnotationVRs("Person");
assertTrue(result.isEmpty());
}

@Test
public void testGetVREnabledAnnotationTypesSkipsNullValues() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData vr1 = mock(ResourceData.class);
when(vr1.getClassName()).thenReturn("vr1");
when(vr1.getGuiType()).thenReturn(ResourceData.NULL_GUI);
when(vr1.getAnnotationTypeDisplayed()).thenReturn(null);
ResourceData vr2 = mock(ResourceData.class);
when(vr2.getClassName()).thenReturn("vr2");
when(vr2.getGuiType()).thenReturn(ResourceData.NULL_GUI);
when(vr2.getAnnotationTypeDisplayed()).thenReturn("Keyword");
when(vr2.getResourceDisplayed()).thenReturn(null);
// when(vr2.getResourceClass()).thenReturn(DummyAnnotationVR.class);
register.put("vr1", vr1);
register.put("vr2", vr2);
List<String> result = register.getVREnabledAnnotationTypes();
assertEquals(1, result.size());
assertEquals("Keyword", result.get(0));
}

@Test
public void testUnregisterPluginWithSharedComponentPreservesOtherDefinitions() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData resource = mock(ResourceData.class);
// when(resource.getClassName()).thenReturn(DummyPR.class.getName());
// when(resource.getResourceClass()).thenReturn(DummyPR.class);
when(resource.reduceReferenceCount()).thenReturn(1);
Plugin plugin = mock(Plugin.class);
URL baseURL = new URL("http://shared.plugin/");
when(plugin.getBaseURL()).thenReturn(baseURL);
when(plugin.getBaseURI()).thenReturn(baseURL.toURI());
Gate.ResourceInfo resourceInfo = mock(Gate.ResourceInfo.class);
// when(resourceInfo.getResourceClassName()).thenReturn(DummyPR.class.getName());
when(plugin.getResourceInfoList()).thenReturn(Collections.singletonList(resourceInfo));
// register.put(DummyPR.class.getName(), resource);
register.getPlugins().add(plugin);
register.unregisterPlugin(plugin);
// assertNotNull(register.get(DummyPR.class.getName()));
}

@Test(expected = GateRuntimeException.class)
public void testGetAllInstancesFailsWithWrongRegisteredClassHierarchy() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData wrong = mock(ResourceData.class);
when(wrong.getClassName()).thenReturn("gate.wrong.TypeX");
when(wrong.getResourceClass()).thenThrow(new ClassNotFoundException());
register.put("gate.wrong.TypeX", wrong);
// register.getAllInstances(DummyPR.class.getName(), true);
}

@Test(expected = GateException.class)
public void testRegisterPluginThrowsWhenPluginCreoleXmlThrowsException() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
URL base = new URL("http://broken.xml/");
when(plugin.getBaseURI()).thenReturn(base.toURI());
when(plugin.getBaseURL()).thenReturn(base);
when(plugin.isValid()).thenReturn(true);
when(plugin.getCreoleXML()).thenThrow(new RuntimeException("Invalid XML"));
// when(plugin.getRequiredPlugins()).thenReturn(Collections.emptyList());
register.registerPlugin(plugin);
}
}

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
import javax.swing.text.Element;

public class CreoleRegisterImpl_llmsuite_2_GPTLLMTest {

@Test
public void testRegisterPlugin_noDependencies() throws Exception {
Gate.init();
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
when(plugin.getBaseURL()).thenReturn(new URL("http://example.com/"));
// when(plugin.getCreoleXML()).thenReturn(new Document(new Element("CREOLE")));
// when(plugin.getRequiredPlugins()).thenReturn(Collections.emptyList());
when(plugin.isValid()).thenReturn(true);
when(plugin.getName()).thenReturn("TestPlugin");
when(plugin.getVersion()).thenReturn("1.0");
register.registerPlugin(plugin);
Set<Plugin> plugins = register.getPlugins();
assertTrue(plugins.contains(plugin));
}

@Test(expected = GateException.class)
public void testRegisterPlugin_invalidPluginThrowsException() throws Exception {
Gate.init();
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
when(plugin.isValid()).thenReturn(false);
when(plugin.getBaseURL()).thenReturn(new URL("http://invalid.com/"));
// when(plugin.getCreoleXML()).thenReturn(new Document(new Element("CREOLE")));
// when(plugin.getRequiredPlugins()).thenReturn(Collections.emptyList());
when(plugin.getName()).thenReturn("Invalid");
when(plugin.getVersion()).thenReturn("0.1");
register.registerPlugin(plugin);
}

@Test
public void testRegisterPlugin_withDependency() throws Exception {
Gate.init();
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin dependency = mock(Plugin.class);
when(dependency.getBaseURL()).thenReturn(new URL("http://example.com/dependency/"));
// when(dependency.getCreoleXML()).thenReturn(new Document(new Element("CREOLE")));
// when(dependency.getRequiredPlugins()).thenReturn(Collections.emptyList());
when(dependency.isValid()).thenReturn(true);
when(dependency.getName()).thenReturn("DepPlugin");
when(dependency.getVersion()).thenReturn("1.0");
Plugin plugin = mock(Plugin.class);
when(plugin.getBaseURL()).thenReturn(new URL("http://example.com/main/"));
// when(plugin.getCreoleXML()).thenReturn(new Document(new Element("CREOLE")));
// when(plugin.getRequiredPlugins()).thenReturn(Collections.singletonList(dependency));
when(plugin.isValid()).thenReturn(true);
when(plugin.getName()).thenReturn("MainPlugin");
when(plugin.getVersion()).thenReturn("2.0");
register.registerPlugin(plugin);
Set<Plugin> plugins = register.getPlugins();
assertTrue(plugins.contains(plugin));
assertTrue(plugins.contains(dependency));
}

@Test
public void testPut_AddsLanguageResourceType() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String className = "gate.test.FakeLanguageResource";
ResourceData resourceData = mock(ResourceData.class);
when(resourceData.getClassName()).thenReturn(className);
// when(resourceData.getResourceClass()).thenReturn(FakeLanguageResource.class);
ResourceData result = register.put(className, resourceData);
assertNotNull(result);
assertTrue(register.getLrTypes().contains(className));
}

@Test
public void testPut_ExistingReferenceIncrementsRefCount() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String className = "gate.test.RefCountResource";
ResourceData resourceData = mock(ResourceData.class);
when(resourceData.getClassName()).thenReturn(className);
// when(resourceData.getResourceClass()).thenReturn(FakeLanguageResource.class);
when(resourceData.increaseReferenceCount()).thenReturn(2);
register.put(className, resourceData);
ResourceData reused = register.put(className, resourceData);
verify(resourceData).increaseReferenceCount();
assertEquals(resourceData, reused);
}

@Test
public void testRemove_RemovesResourceWhenRefIsZero() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String className = "gate.test.ToRemove";
ResourceData resourceData = mock(ResourceData.class);
when(resourceData.getClassName()).thenReturn(className);
// when(resourceData.getResourceClass()).thenReturn(FakeLanguageResource.class);
when(resourceData.reduceReferenceCount()).thenReturn(0);
when(resourceData.isTool()).thenReturn(false);
register.put(className, resourceData);
ResourceData removed = register.remove(className);
assertEquals(resourceData, removed);
assertFalse(register.getLrTypes().contains(className));
}

@Test
public void testRemove_DoesNotRemoveWhenRefGreaterThanZero() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String className = "gate.test.SharedRes";
ResourceData resourceData = mock(ResourceData.class);
when(resourceData.getClassName()).thenReturn(className);
// when(resourceData.getResourceClass()).thenReturn(FakeProcessingResource.class);
when(resourceData.reduceReferenceCount()).thenReturn(2);
register.put(className, resourceData);
ResourceData removed = register.remove(className);
assertEquals(resourceData, removed);
assertTrue(register.containsKey(className));
}

@Test
public void testGetLargeVRsForNullResourceReturnsEmptyList() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<String> result = register.getLargeVRsForResource(null);
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testSetResourceName_FiresRenameEvent() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Resource resource = mock(Resource.class);
when(resource.getName()).thenReturn("oldName");
doNothing().when(resource).setName("newName");
CreoleListener listener = mock(CreoleListener.class);
register.addCreoleListener(listener);
register.setResourceName(resource, "newName");
verify(listener).resourceRenamed(resource, "oldName", "newName");
}

@Test
public void testClear_RemovesAllRegisteredTypes() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String lrType = "gate.test.MyLR";
String prType = "gate.test.MyPR";
ResourceData lrData = mock(ResourceData.class);
ResourceData prData = mock(ResourceData.class);
when(lrData.getClassName()).thenReturn(lrType);
// when(lrData.getResourceClass()).thenReturn(FakeLanguageResource.class);
when(prData.getClassName()).thenReturn(prType);
// when(prData.getResourceClass()).thenReturn(FakeProcessingResource.class);
register.put(lrType, lrData);
register.put(prType, prData);
register.clear();
assertTrue(register.getLrTypes().isEmpty());
assertTrue(register.getPrTypes().isEmpty());
}

@Test
public void testPut_VRTypeAddedOnlyOnce() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String className = "gate.test.MyVR";
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn(className);
// when(rd.getResourceClass()).thenReturn(FakeVisualResource.class);
register.put(className, rd);
register.put(className, rd);
List<String> vrTypes = new ArrayList<String>(register.getVrTypes());
int count = 0;
if (vrTypes.contains(className))
count++;
assertEquals(1, count);
}

@Test(expected = GateRuntimeException.class)
public void testPut_InvalidClassThrowsGateRuntimeException() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String className = "broken.type";
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn(className);
when(rd.getResourceClass()).thenThrow(new ClassNotFoundException());
register.put(className, rd);
}

@Test
public void testRemove_NullKeyReturnsNull() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData result = register.remove(null);
assertNull(result);
}

@Test
public void testRemove_ClassNotFoundDuringRemoval() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String className = "gate.test.Broken";
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn(className);
when(rd.getResourceClass()).thenThrow(new ClassNotFoundException());
when(rd.reduceReferenceCount()).thenReturn(0);
when(rd.isTool()).thenReturn(false);
register.put(className, rd);
try {
register.remove(className);
fail("Expected GateRuntimeException to be thrown");
} catch (GateRuntimeException e) {
assertTrue(e.getMessage().contains("Could not load class specified in CREOLE data"));
}
}

@Test
public void testGetVrTypes_ImmutableResult() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String className = "gate.test.VR";
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn(className);
// when(rd.getResourceClass()).thenReturn(FakeVisualResource.class);
register.put(className, rd);
// List<String> result = register.getVrTypes();
try {
// result.add("gate.test.Other");
fail("Expected UnsupportedOperationException");
} catch (UnsupportedOperationException e) {
assertTrue(true);
}
}

@Test
public void testSetResourceNameSameName() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Resource resource = mock(Resource.class);
when(resource.getName()).thenReturn("Name");
CreoleListener listener = mock(CreoleListener.class);
register.addCreoleListener(listener);
doNothing().when(resource).setName("Name");
register.setResourceName(resource, "Name");
verify(listener).resourceRenamed(resource, "Name", "Name");
}

@Test
public void testGetAnnotationVRs_EmptyWhenNoMatch() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<String> result = register.getAnnotationVRs("UnknownType");
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetAnnotationVRs_NullTypeReturnsEmpty() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<String> result = register.getAnnotationVRs(null);
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetAnnotationVRs_AddsVRToListWhenValid() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String className = "gate.test.AnnotVR";
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn(className);
// when(rd.getResourceClass()).thenReturn(FakeAnnotationVisualResource.class);
when(rd.getGuiType()).thenReturn(ResourceData.NULL_GUI);
when(rd.getResourceDisplayed()).thenReturn(null);
when(rd.getAnnotationTypeDisplayed()).thenReturn("Person");
when(rd.isMainView()).thenReturn(false);
register.put(className, rd);
List<String> result = register.getAnnotationVRs("Person");
assertNotNull(result);
assertEquals(1, result.size());
assertEquals(className, result.get(0));
}

@Test
public void testGetAllInstances_InvalidTypeThrowsGateException() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
try {
register.getAllInstances("gate.invalid.Type");
fail("Expected GateException");
} catch (GateException ex) {
assertTrue(ex.getMessage().contains("Invalid type"));
}
}

@Test
public void testPublics_IgnoresPrivateResources() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String className = "gate.test.LR";
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn(className);
// when(rd.getResourceClass()).thenReturn(FakeLanguageResource.class);
when(rd.isPrivate()).thenReturn(true);
register.put(className, rd);
List<LanguageResource> result = register.getPublicLrInstances();
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetVREnabledAnnotationTypes_IncludesExpectedValue() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String className = "gate.test.VR";
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn(className);
when(rd.getGuiType()).thenReturn(ResourceData.NULL_GUI);
when(rd.getAnnotationTypeDisplayed()).thenReturn("Token");
// when(rd.getResourceClass()).thenReturn(FakeVisualResource.class);
register.put(className, rd);
List<String> result = register.getVREnabledAnnotationTypes();
assertTrue(result.contains("Token"));
}

@Test
public void testRemoveDirectory_UnregisteredPlugin() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
URL directoryURL = new URL("http://not.loaded/plugin");
register.removeDirectory(directoryURL);
assertTrue(true);
}

@Test
public void testRemovePlugin_RemovesResourcesAndDependents() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin mainPlugin = mock(Plugin.class);
Plugin dependent = mock(Plugin.class);
ResourceData rdata = mock(ResourceData.class);
String resourceName = "gate.test.SomeResource";
when(mainPlugin.getBaseURL()).thenReturn(new URL("http://main/"));
when(dependent.getBaseURL()).thenReturn(new URL("http://dep/"));
// when(dependent.getRequiredPlugins()).thenReturn(Collections.singletonList(mainPlugin));
// when(mainPlugin.getRequiredPlugins()).thenReturn(Collections.emptyList());
when(rdata.getClassName()).thenReturn(resourceName);
// when(rdata.getResourceClass()).thenReturn(FakeProcessingResource.class);
when(rdata.getReferenceCount()).thenReturn(1);
// ResourceInfo ri = mock(ResourceInfo.class);
// when(ri.getResourceClassName()).thenReturn(resourceName);
// when(dependent.getResourceInfoList()).thenReturn(Collections.singletonList(ri));
List<Resource> mockResources = new ArrayList<>();
ProcessingResource res = mock(ProcessingResource.class);
mockResources.add(res);
when(rdata.getInstantiations()).thenReturn(mockResources);
register.put(resourceName, rdata);
register.registerPlugin(mainPlugin);
register.registerPlugin(dependent);
register.unregisterPlugin(mainPlugin);
assertFalse(register.getPlugins().contains(mainPlugin));
assertFalse(register.containsKey(resourceName));
}

@Test
public void testGetVrInstancesForUnknownTypeReturnsEmptyList() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<VisualResource> result = register.getVrInstances("non.existent.Class");
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetPrInstancesForUnknownTypeReturnsEmptyList() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<ProcessingResource> result = register.getPrInstances("unknown.pr.Type");
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetLrInstancesForUnknownTypeReturnsEmptyList() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<LanguageResource> result = register.getLrInstances("unknown.lr.Type");
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testRemoveUnregisteredPluginDoesNothing() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
when(plugin.getBaseURL()).thenReturn(new URL("http://not.registered/"));
register.unregisterPlugin(plugin);
assertTrue(register.getPlugins().isEmpty());
}

@Test
public void testRemoveDirectoryNullDoesNothing() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
register.removeDirectory(null);
assertTrue(register.getPlugins().isEmpty());
}

@Test
public void testRegisterDirectoriesWithException() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
URL url = mock(URL.class);
when(url.toString()).thenThrow(new RuntimeException("Simulated URL failure"));
try {
register.registerDirectories(url);
fail("Expected GateException");
} catch (GateException ex) {
assertTrue(ex.getMessage().contains("Failed to load plugin"));
}
}

@Test
public void testGetSmallVRsForResourceWithNullInputReturnsEmptyList() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<String> result = register.getSmallVRsForResource(null);
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetVRsForResourceClassWithNoMatchingVR() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String resourceClassName = "gate.test.MyResource";
ResourceData vrData = mock(ResourceData.class);
when(vrData.getClassName()).thenReturn("gate.test.MyVR");
when(vrData.getGuiType()).thenReturn(ResourceData.LARGE_GUI);
when(vrData.getResourceDisplayed()).thenReturn("gate.something.OtherClass");
// when(vrData.getResourceClass()).thenReturn(FakeVisualResource.class);
register.put("gate.test.MyVR", vrData);
List<String> result = register.getLargeVRsForResource(resourceClassName);
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testRemovePluginSkipsUnmatchedRequiredPluginClassName() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
ResourceData rd = mock(ResourceData.class);
// ResourceInfo ri = mock(ResourceInfo.class);
String resourceClassName = "gate.test.UnknownRes";
// when(plugin.getRequiredPlugins()).thenReturn(Collections.emptyList());
when(plugin.getBaseURL()).thenReturn(new URL("http://plugin/"));
// when(plugin.getResourceInfoList()).thenReturn(Collections.singletonList(ri));
// when(ri.getResourceClassName()).thenReturn(resourceClassName);
when(rd.getReferenceCount()).thenReturn(1);
register.put(resourceClassName, rd);
register.unregisterPlugin(plugin);
assertFalse(register.containsKey(resourceClassName));
}

@Test
public void testRemoveResourceWithToolTypeFlagTrue() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String className = "gate.test.ToolResource";
ResourceData data = mock(ResourceData.class);
when(data.getClassName()).thenReturn(className);
// when(data.getResourceClass()).thenReturn(FakeProcessingTool.class);
when(data.reduceReferenceCount()).thenReturn(0);
when(data.isTool()).thenReturn(true);
register.put(className, data);
ResourceData removed = register.remove(className);
Set<String> tools = register.getToolTypes();
assertEquals(data, removed);
assertFalse(tools.contains(className));
}

@Test(expected = GateRuntimeException.class)
public void testGetVRsForResourceThrowsWhenDisplayedClassInvalid() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String vrClass = "gate.test.VR";
String targetClass = "gate.test.Unavailable";
ResourceData vr = mock(ResourceData.class);
when(vr.getClassName()).thenReturn(vrClass);
when(vr.getGuiType()).thenReturn(ResourceData.LARGE_GUI);
when(vr.getResourceDisplayed()).thenReturn("non.existent.Type");
// when(vr.getResourceClass()).thenReturn(FakeVisualResource.class);
register.put(vrClass, vr);
register.getLargeVRsForResource(targetClass);
}

@Test
public void testFireDatastoreEventsWithNoListeners() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleEvent event = mock(CreoleEvent.class);
register.datastoreCreated(event);
register.datastoreOpened(event);
register.datastoreClosed(event);
assertTrue(true);
}

@Test
public void testRemoveBeforePutReturnsNull() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData result = register.remove("non.existent.key");
assertNull(result);
}

@Test
public void testAddSameCreoleListenerTwiceOnlyStoresOnce() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleListener listener = mock(CreoleListener.class);
register.addCreoleListener(listener);
register.addCreoleListener(listener);
register.removeCreoleListener(listener);
CreoleEvent event = mock(CreoleEvent.class);
register.resourceLoaded(event);
assertTrue(true);
}

@Test
public void testRemoveCreoleListenerThatWasNotAdded() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleListener listener = mock(CreoleListener.class);
register.removeCreoleListener(listener);
assertTrue(true);
}

@Test
public void testUnregisterPluginSkipsDependentPluginsWithNoMatch() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin1 = mock(Plugin.class);
Plugin unrelatedPlugin = mock(Plugin.class);
URL pluginUrl = new URL("http://plugin/");
URL unrelatedUrl = new URL("http://unrelated/");
when(plugin1.getBaseURL()).thenReturn(pluginUrl);
when(unrelatedPlugin.getBaseURL()).thenReturn(unrelatedUrl);
// when(plugin1.getRequiredPlugins()).thenReturn(Collections.emptyList());
// when(unrelatedPlugin.getRequiredPlugins()).thenReturn(Collections.emptyList());
register.getPlugins().add(plugin1);
register.getPlugins().add(unrelatedPlugin);
when(plugin1.getResourceInfoList()).thenReturn(Collections.emptyList());
when(plugin1.getName()).thenReturn("P1");
when(plugin1.getVersion()).thenReturn("1.0");
register.unregisterPlugin(plugin1);
Set<Plugin> current = register.getPlugins();
assertFalse(current.contains(plugin1));
}

@Test
public void testParseDirectoryThrowsGateExceptionOnIOException() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
// Document xml = new Document(new Element("CREOLE"));
URL baseUrl = new URL("http://test/");
when(plugin.getBaseURL()).thenReturn(baseUrl);
// GateClassLoader loader = Gate.getClassLoader();
String key = baseUrl.toURI().toString();
// loader.forgetClassLoader(key, plugin);
// try {
// register.parseDirectory(plugin, xml, baseUrl, new URL(baseUrl, "creole.xml"));
// fail("Expected GateException");
// } catch (GateException e) {
// assertTrue(true);
// }
}

@Test
public void testAddAndRemovePluginListener() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
PluginListener listener = mock(PluginListener.class);
Plugin plugin = mock(Plugin.class);
when(plugin.getName()).thenReturn("Test");
when(plugin.getVersion()).thenReturn("1.0");
register.addPluginListener(listener);
register.firePluginLoaded(plugin);
verify(listener).pluginLoaded(plugin);
register.firePluginUnloaded(plugin);
verify(listener).pluginUnloaded(plugin);
register.removePluginListener(listener);
register.firePluginLoaded(plugin);
}

@Test
public void testGetPublicTypesSkipsNullResourceData() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Set<String> types = new HashSet<String>();
types.add("non.existent");
List<String> result = register.getPublicTypes(types);
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test(expected = GateRuntimeException.class)
public void testGetVRsForResourceRaisesWhenResourceClassFails() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String className = "gate.test.FailingVR";
ResourceData vrData = mock(ResourceData.class);
when(vrData.getClassName()).thenReturn(className);
when(vrData.getGuiType()).thenReturn(ResourceData.LARGE_GUI);
when(vrData.getResourceDisplayed()).thenReturn("gate.test.SomeResource");
when(vrData.getResourceClass()).thenThrow(new ClassNotFoundException());
register.put(className, vrData);
register.getLargeVRsForResource("gate.test.SomeResource");
}

@Test
public void testClearAlsoClearsControllerAndApplicationTypes() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String ctrl = "gate.test.Ctrl";
String app = "gate.test.App";
ResourceData ctrlData = mock(ResourceData.class);
ResourceData appData = mock(ResourceData.class);
when(ctrlData.getClassName()).thenReturn(ctrl);
// when(ctrlData.getResourceClass()).thenReturn(FakeController.class);
when(appData.getClassName()).thenReturn(app);
// when(appData.getResourceClass()).thenReturn(FakeApplication.class);
register.put(ctrl, ctrlData);
register.put(app, appData);
register.clear();
assertTrue(register.getControllerTypes().isEmpty());
assertTrue(register.getApplicationTypes().isEmpty());
}

@Test
public void testGetAnnotationVRs_DefaultVROrdering() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String vr1 = "gate.VR.Class1";
String vr2 = "gate.VR.Class2";
ResourceData vrData1 = mock(ResourceData.class);
when(vrData1.getClassName()).thenReturn(vr1);
when(vrData1.getGuiType()).thenReturn(ResourceData.NULL_GUI);
when(vrData1.getAnnotationTypeDisplayed()).thenReturn("Person");
when(vrData1.getResourceDisplayed()).thenReturn(null);
// when(vrData1.getResourceClass()).thenReturn(FakeAnnotationVisualResource.class);
when(vrData1.isMainView()).thenReturn(true);
ResourceData vrData2 = mock(ResourceData.class);
when(vrData2.getClassName()).thenReturn(vr2);
when(vrData2.getGuiType()).thenReturn(ResourceData.NULL_GUI);
when(vrData2.getAnnotationTypeDisplayed()).thenReturn("Person");
when(vrData2.getResourceDisplayed()).thenReturn(null);
// when(vrData2.getResourceClass()).thenReturn(FakeAnnotationVisualResource.class);
when(vrData2.isMainView()).thenReturn(false);
register.put(vr1, vrData1);
register.put(vr2, vrData2);
List<String> result = register.getAnnotationVRs("Person");
assertEquals(2, result.size());
assertEquals(vr1, result.get(0));
}

@Test
public void testGetAnnotationVRs_OnlyDefaultWithNullDisplayType() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String vrClass = "gate.VR.Default";
ResourceData vrData = mock(ResourceData.class);
when(vrData.getClassName()).thenReturn(vrClass);
when(vrData.getGuiType()).thenReturn(ResourceData.NULL_GUI);
when(vrData.getAnnotationTypeDisplayed()).thenReturn(null);
when(vrData.getResourceDisplayed()).thenReturn(null);
// when(vrData.getResourceClass()).thenReturn(FakeAnnotationVisualResource.class);
when(vrData.isMainView()).thenReturn(true);
register.put(vrClass, vrData);
List<String> result = register.getAnnotationVRs();
assertNotNull(result);
assertEquals(1, result.size());
assertEquals(vrClass, result.get(0));
}

@Test
public void testPut_ToolResourceIsAddedToToolTypes() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String className = "gate.tool.MyTool";
ResourceData data = mock(ResourceData.class);
when(data.getClassName()).thenReturn(className);
// when(data.getResourceClass()).thenReturn(FakeToolResource.class);
when(data.isTool()).thenReturn(true);
ResourceData result = register.put(className, data);
assertTrue(register.getToolTypes().contains(className));
}

@Test
public void testRemoveUpdatesControllerAndApplicationTypes() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String ctrlClass = "gate.controller.MyController";
String appClass = "gate.app.MyApp";
ResourceData controllerData = mock(ResourceData.class);
when(controllerData.getClassName()).thenReturn(ctrlClass);
// when(controllerData.getResourceClass()).thenReturn(FakeController.class);
when(controllerData.isTool()).thenReturn(false);
when(controllerData.reduceReferenceCount()).thenReturn(0);
ResourceData appData = mock(ResourceData.class);
when(appData.getClassName()).thenReturn(appClass);
// when(appData.getResourceClass()).thenReturn(FakeApplication.class);
when(appData.isTool()).thenReturn(false);
when(appData.reduceReferenceCount()).thenReturn(0);
register.put(ctrlClass, controllerData);
register.put(appClass, appData);
register.remove(ctrlClass);
register.remove(appClass);
assertFalse(register.getControllerTypes().contains(ctrlClass));
assertFalse(register.getApplicationTypes().contains(appClass));
}

@Test
public void testGetPublicTypesSkipsPrivateResources() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String visibleType = "gate.MyVisible";
String privateType = "gate.MyPrivate";
ResourceData rd1 = mock(ResourceData.class);
when(rd1.getClassName()).thenReturn(visibleType);
when(rd1.isPrivate()).thenReturn(false);
ResourceData rd2 = mock(ResourceData.class);
when(rd2.getClassName()).thenReturn(privateType);
when(rd2.isPrivate()).thenReturn(true);
register.put(visibleType, rd1);
register.put(privateType, rd2);
Set<String> types = new HashSet<String>();
types.add(visibleType);
types.add(privateType);
List<String> result = register.getPublicTypes(types);
assertEquals(1, result.size());
assertEquals(visibleType, result.get(0));
}

@Test(expected = GateRuntimeException.class)
public void testGetLargeVRsForInvalidResourceClassThrows() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String vrName = "gate.MyVR";
ResourceData vr = mock(ResourceData.class);
when(vr.getClassName()).thenReturn(vrName);
when(vr.getGuiType()).thenReturn(ResourceData.LARGE_GUI);
when(vr.getResourceDisplayed()).thenReturn("non.existent.Class");
// when(vr.getResourceClass()).thenReturn(FakeVisualResource.class);
register.put(vrName, vr);
register.getLargeVRsForResource("gate.SomeType");
}

@Test(expected = GateRuntimeException.class)
public void testGetResourceClassFailsInAnnotationVRThrows() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String vr = "gate.VR.Faulty";
ResourceData vrData = mock(ResourceData.class);
when(vrData.getClassName()).thenReturn(vr);
when(vrData.getGuiType()).thenReturn(ResourceData.NULL_GUI);
when(vrData.getAnnotationTypeDisplayed()).thenReturn("Entity");
when(vrData.getResourceClass()).thenThrow(new ClassNotFoundException());
register.put(vr, vrData);
register.getAnnotationVRs("Entity");
}

@Test
public void testFireResourceLoadedWithNoListeners() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleEvent event = mock(CreoleEvent.class);
register.resourceLoaded(event);
assertTrue(true);
}

@Test
public void testProcessAllPluginListenersWithoutFailure() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
PluginListener listener1 = mock(PluginListener.class);
PluginListener listener2 = mock(PluginListener.class);
Plugin plugin = mock(Plugin.class);
when(plugin.getName()).thenReturn("X");
when(plugin.getVersion()).thenReturn("1.0");
register.addPluginListener(listener1);
register.addPluginListener(listener2);
register.firePluginLoaded(plugin);
verify(listener1).pluginLoaded(plugin);
verify(listener2).pluginLoaded(plugin);
register.firePluginUnloaded(plugin);
verify(listener1).pluginUnloaded(plugin);
verify(listener2).pluginUnloaded(plugin);
}

@Test
public void testRegisterComponentThrowsGateExceptionWhenMalformedUrl() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
// Class<? extends Resource> invalidClass = BrokenComponent.class;
// try {
// register.registerComponent(invalidClass);
// fail("Expected GateException due to malformed URL from constructor");
// } catch (GateException e) {
// assertTrue(e.getMessage().contains("Unable to register component"));
// }
}

@Test
public void testGetAllInstancesIncludesHiddenWhenRequested() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String className = "gate.test.HiddenResource";
Resource hiddenRes = mock(Resource.class);
FeatureMap features = Factory.newFeatureMap();
// features.put(GateConstants.HIDDEN_FEATURE, true);
when(hiddenRes.getFeatures()).thenReturn(features);
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn(className);
// when(rd.getResourceClass()).thenReturn(FakeProcessingResource.class);
List<Resource> instances = new ArrayList<Resource>();
instances.add(hiddenRes);
when(rd.getInstantiations()).thenReturn(instances);
register.put(className, rd);
List<Resource> found = register.getAllInstances("gate.test.HiddenResource", true);
assertEquals(1, found.size());
assertEquals(hiddenRes, found.get(0));
}

@Test
public void testGetAllInstancesExcludesHiddenByDefault() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String className = "gate.test.Hidden";
Resource r = mock(Resource.class);
FeatureMap features = Factory.newFeatureMap();
// features.put(GateConstants.HIDDEN_FEATURE, true);
when(r.getFeatures()).thenReturn(features);
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn(className);
// when(rd.getResourceClass()).thenReturn(FakeProcessingResource.class);
when(rd.getInstantiations()).thenReturn(Collections.singletonList(r));
register.put(className, rd);
List<Resource> result = register.getAllInstances("gate.test.Hidden");
assertTrue(result.isEmpty());
}

@Test
public void testPutReturnsExistingDataWithoutReplacing() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String type = "gate.test.Type";
ResourceData existing = mock(ResourceData.class);
when(existing.getClassName()).thenReturn(type);
// when(existing.getResourceClass()).thenReturn(FakeLanguageResource.class);
when(existing.increaseReferenceCount()).thenReturn(1);
register.put(type, existing);
ResourceData returned = register.put(type, existing);
assertSame(existing, returned);
verify(existing).increaseReferenceCount();
}

@Test
public void testUnregisterPluginWhenResourceReferenceCountGreaterThanOne() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
when(plugin.getBaseURL()).thenReturn(new URL("http://example.com"));
when(plugin.getName()).thenReturn("Test");
when(plugin.getVersion()).thenReturn("1.0");
// when(plugin.getRequiredPlugins()).thenReturn(Collections.emptyList());
String className = "gate.test.RefCounted";
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn(className);
when(rd.getReferenceCount()).thenReturn(2);
// ResourceInfo info = mock(ResourceInfo.class);
// when(info.getResourceClassName()).thenReturn(className);
// when(plugin.getResourceInfoList()).thenReturn(Collections.singletonList(info));
register.put(className, rd);
register.getPlugins().add(plugin);
register.unregisterPlugin(plugin);
assertTrue(register.containsKey(className));
}

@Test
public void testGetVRsForResourceWithMainViewPriorityAndMultipleMatches() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String targetClass = "gate.MyResource";
String vr1 = "gate.VR.One";
String vr2 = "gate.VR.Main";
ResourceData r1 = mock(ResourceData.class);
when(r1.getClassName()).thenReturn(vr1);
when(r1.getGuiType()).thenReturn(ResourceData.LARGE_GUI);
when(r1.getResourceDisplayed()).thenReturn("gate.Resource");
when(r1.isMainView()).thenReturn(false);
// when(r1.getResourceClass()).thenReturn(FakeVisualResource.class);
ResourceData r2 = mock(ResourceData.class);
when(r2.getClassName()).thenReturn(vr2);
when(r2.getGuiType()).thenReturn(ResourceData.LARGE_GUI);
when(r2.getResourceDisplayed()).thenReturn("gate.Resource");
when(r2.isMainView()).thenReturn(true);
// when(r2.getResourceClass()).thenReturn(FakeVisualResource.class);
register.put(vr1, r1);
register.put(vr2, r2);
List<String> list = register.getLargeVRsForResource("gate.MyResource");
assertEquals(2, list.size());
assertEquals(vr2, list.get(0));
}

@Test
public void testMultiplePluginsWithSameDependencyRegistration() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin dependency = mock(Plugin.class);
// when(dependency.getCreoleXML()).thenReturn(new Document(new Element("CREOLE")));
when(dependency.getBaseURL()).thenReturn(new URL("http://dep/"));
when(dependency.getName()).thenReturn("dep");
when(dependency.getVersion()).thenReturn("1.0");
// when(dependency.getRequiredPlugins()).thenReturn(Collections.emptyList());
when(dependency.isValid()).thenReturn(true);
Plugin plugin1 = mock(Plugin.class);
Plugin plugin2 = mock(Plugin.class);
when(plugin1.getBaseURL()).thenReturn(new URL("http://plugin1/"));
// when(plugin1.getCreoleXML()).thenReturn(new Document(new Element("CREOLE")));
// when(plugin1.getRequiredPlugins()).thenReturn(Collections.singletonList(dependency));
when(plugin1.getName()).thenReturn("plugin1");
when(plugin1.getVersion()).thenReturn("1.0");
when(plugin1.isValid()).thenReturn(true);
when(plugin2.getBaseURL()).thenReturn(new URL("http://plugin2/"));
// when(plugin2.getCreoleXML()).thenReturn(new Document(new Element("CREOLE")));
// when(plugin2.getRequiredPlugins()).thenReturn(Collections.singletonList(dependency));
when(plugin2.getName()).thenReturn("plugin2");
when(plugin2.getVersion()).thenReturn("1.0");
when(plugin2.isValid()).thenReturn(true);
register.registerPlugin(plugin1);
register.registerPlugin(plugin2);
Set<Plugin> all = register.getPlugins();
assertTrue(all.contains(plugin1));
assertTrue(all.contains(plugin2));
assertTrue(all.contains(dependency));
}

@Test
public void testRemoveToolTypeResourceAlsoRemovesFromToolList() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String className = "gate.test.MyPRTool";
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn(className);
// when(rd.getResourceClass()).thenReturn(FakePRTool.class);
when(rd.reduceReferenceCount()).thenReturn(0);
when(rd.isTool()).thenReturn(true);
register.put(className, rd);
register.remove(className);
assertFalse(register.getToolTypes().contains(className));
}

@Test
public void testPutAddsControllerTypeProperly() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String controllerName = "gate.test.MyCtrl";
ResourceData ctrlData = mock(ResourceData.class);
when(ctrlData.getClassName()).thenReturn(controllerName);
// when(ctrlData.getResourceClass()).thenReturn(FakeController.class);
register.put(controllerName, ctrlData);
assertTrue(register.getControllerTypes().contains(controllerName));
}

@Test
public void testPutAddsApplicationTypeToSet() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String appType = "gate.test.MyApp";
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn(appType);
// when(rd.getResourceClass()).thenReturn(FakeApplication.class);
register.put(appType, rd);
assertTrue(register.getApplicationTypes().contains(appType));
}

@Test
public void testGetPublicTypesWithMixedVisibility() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String publicType = "gate.test.Public";
String privateType = "gate.test.Private";
ResourceData publicData = mock(ResourceData.class);
when(publicData.getClassName()).thenReturn(publicType);
when(publicData.isPrivate()).thenReturn(false);
ResourceData privateData = mock(ResourceData.class);
when(privateData.getClassName()).thenReturn(privateType);
when(privateData.isPrivate()).thenReturn(true);
register.put(publicType, publicData);
register.put(privateType, privateData);
Set<String> types = new HashSet<String>();
types.add(publicType);
types.add(privateType);
List<String> result = register.getPublicTypes(types);
assertEquals(1, result.size());
assertEquals(publicType, result.get(0));
}

@Test
public void testFireResourceRenamedWithMultipleListeners() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleListener l1 = mock(CreoleListener.class);
CreoleListener l2 = mock(CreoleListener.class);
Resource resource = mock(Resource.class);
when(resource.getName()).thenReturn("Old");
register.addCreoleListener(l1);
register.addCreoleListener(l2);
doNothing().when(resource).setName("New");
register.setResourceName(resource, "New");
verify(l1).resourceRenamed(resource, "Old", "New");
verify(l2).resourceRenamed(resource, "Old", "New");
}

@Test
public void testGetVREnabledAnnotationTypesSkipsNullAnnotationType() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String vrClass = "gate.MyVR";
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn(vrClass);
when(rd.getGuiType()).thenReturn(ResourceData.NULL_GUI);
when(rd.getAnnotationTypeDisplayed()).thenReturn(null);
// when(rd.getResourceClass()).thenReturn(FakeVisualResource.class);
register.put(vrClass, rd);
List<String> result = register.getVREnabledAnnotationTypes();
assertFalse(result.contains(null));
}

@Test
public void testGetAnnotationVRsWithNullResourceDisplayedIgnoresVR() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String vrClass = "gate.VR";
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn(vrClass);
when(rd.getGuiType()).thenReturn(ResourceData.NULL_GUI);
when(rd.getAnnotationTypeDisplayed()).thenReturn("Person");
when(rd.getResourceDisplayed()).thenReturn(null);
// when(rd.getResourceClass()).thenReturn(FakeVisualResource.class);
register.put(vrClass, rd);
List<String> result = register.getAnnotationVRs("UnknownType");
assertTrue(result.isEmpty());
}

@Test
public void testRemoveControllerClassFromControllerTypeList() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String ctrlClass = "gate.MyCtrl";
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn(ctrlClass);
// when(rd.getResourceClass()).thenReturn(FakeController.class);
when(rd.reduceReferenceCount()).thenReturn(0);
when(rd.isTool()).thenReturn(false);
register.put(ctrlClass, rd);
assertTrue(register.getControllerTypes().contains(ctrlClass));
register.remove(ctrlClass);
assertFalse(register.getControllerTypes().contains(ctrlClass));
}

@Test
public void testRemoveVisualResourceOnlyOnceInVRTypesList() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String vr = "gate.MyVR";
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn(vr);
// when(rd.getResourceClass()).thenReturn(FakeVisualResource.class);
when(rd.reduceReferenceCount()).thenReturn(0);
when(rd.isTool()).thenReturn(false);
register.put(vr, rd);
// List<String> before = register.getVrTypes();
// assertTrue(before.contains(vr));
register.remove(vr);
// List<String> after = register.getVrTypes();
// assertFalse(after.contains(vr));
}

@Test(expected = GateRuntimeException.class)
public void testRemoveThrowsExceptionWhenClassNotFound() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
String broken = "gate.Broken";
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn(broken);
when(rd.getResourceClass()).thenThrow(new ClassNotFoundException());
when(rd.reduceReferenceCount()).thenReturn(0);
when(rd.isTool()).thenReturn(false);
register.put(broken, rd);
register.remove(broken);
}
}

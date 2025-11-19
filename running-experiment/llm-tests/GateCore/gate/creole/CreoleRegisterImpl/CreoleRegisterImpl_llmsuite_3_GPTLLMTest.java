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

public class CreoleRegisterImpl_llmsuite_3_GPTLLMTest {

@Test
public void testPutNewLanguageResourceAddsToLrTypes() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn("my.MockLR");
// when(rd.getResourceClass()).thenReturn(MyLanguageResource.class);
when(rd.isTool()).thenReturn(true);
ResourceData result = register.put("my.MockLR", rd);
assertNull(result);
assertTrue(register.getLrTypes().contains("my.MockLR"));
assertTrue(register.getToolTypes().contains("my.MockLR"));
}

@Test
public void testPutSameKeyIncreasesReferenceCount() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn("my.MockLR");
// when(rd.getResourceClass()).thenReturn(MyLanguageResource.class);
when(rd.isTool()).thenReturn(false);
when(rd.increaseReferenceCount()).thenReturn(2);
ResourceData first = register.put("my.MockLR", rd);
ResourceData second = register.put("my.MockLR", rd);
assertNull(first);
assertEquals(rd, second);
verify(rd).increaseReferenceCount();
}

@Test(expected = GateRuntimeException.class)
public void testPutThrowsWhenClassNotFound() {
// CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn("invalid.ClassName");
try {
when(rd.getResourceClass()).thenThrow(new ClassNotFoundException("fail"));
} catch (ClassNotFoundException e) {
}
// register.put("invalid.ClassName", rd);
}

@Test
public void testRemoveReducesReferenceCount() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn("mock.MockPR");
// when(rd.getResourceClass()).thenReturn(MyProcessingResource.class);
when(rd.reduceReferenceCount()).thenReturn(1);
register.put("mock.MockPR", rd);
ResourceData removed = register.remove("mock.MockPR");
assertEquals(rd, removed);
verify(rd).reduceReferenceCount();
}

@Test
public void testRemoveFullyRemovesItemWhenRefCountZero() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn("mock.MockVR");
// when(rd.getResourceClass()).thenReturn(MyVisualResource.class);
when(rd.reduceReferenceCount()).thenReturn(0);
when(rd.isTool()).thenReturn(false);
register.put("mock.MockVR", rd);
ResourceData result = register.remove("mock.MockVR");
assertEquals(rd, result);
assertFalse(register.getVrTypes().contains("mock.MockVR"));
}

@Test
public void testClearRemovesAllRegisteredTypes() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd1 = mock(ResourceData.class);
when(rd1.getClassName()).thenReturn("a.MockLR");
// when(rd1.getResourceClass()).thenReturn(MyLanguageResource.class);
when(rd1.isTool()).thenReturn(false);
ResourceData rd2 = mock(ResourceData.class);
when(rd2.getClassName()).thenReturn("b.MockVR");
// when(rd2.getResourceClass()).thenReturn(MyVisualResource.class);
when(rd2.isTool()).thenReturn(false);
register.put("a.MockLR", rd1);
register.put("b.MockVR", rd2);
register.clear();
assertTrue(register.getLrTypes().isEmpty());
assertTrue(register.getVrTypes().isEmpty());
assertTrue(register.getPrTypes().isEmpty());
}

@Test
public void testRegisterComponentRegistersLanguageResource() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
// register.registerComponent(MyLanguageResource.class);
Set<String> types = register.getLrTypes();
assertEquals(1, types.size());
assertTrue(types.iterator().next().contains("MyLanguageResource"));
}

@Test
public void testGetDirectoriesAlwaysReturnsEmptySet() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Set<URL> dirs = register.getDirectories();
assertNotNull(dirs);
assertTrue(dirs.isEmpty());
}

@Test
public void testSetResourceNameInvokesRenamedEvent() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleListener listener = mock(CreoleListener.class);
Resource res = mock(Resource.class);
when(res.getName()).thenReturn("oldName");
register.addCreoleListener(listener);
register.setResourceName(res, "newName");
verify(res).setName("newName");
verify(listener).resourceRenamed(res, "oldName", "newName");
}

@Test
public void testAddAndRemoveCreoleListener() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleListener listener = mock(CreoleListener.class);
register.addCreoleListener(listener);
register.removeCreoleListener(listener);
}

@Test
public void testAddAndRemovePluginListenerAndFire() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
PluginListener pluginListener = mock(PluginListener.class);
Plugin plugin = mock(Plugin.class);
register.addPluginListener(pluginListener);
register.firePluginLoaded(plugin);
register.removePluginListener(pluginListener);
register.firePluginUnloaded(plugin);
verify(pluginListener).pluginLoaded(plugin);
verify(pluginListener).pluginUnloaded(plugin);
}

@Test
public void testGetVREnabledAnnotationTypesWhenNoDataReturnsEmptyList() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<String> result = register.getVREnabledAnnotationTypes();
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetPublicTypesFiltersBasedOnAccessModifier() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData privateType = mock(ResourceData.class);
when(privateType.getClassName()).thenReturn("priv.Type");
// when(privateType.getResourceClass()).thenReturn(MyVisualResource.class);
when(privateType.isPrivate()).thenReturn(true);
ResourceData publicType = mock(ResourceData.class);
when(publicType.getClassName()).thenReturn("pub.Type");
// when(publicType.getResourceClass()).thenReturn(MyVisualResource.class);
when(publicType.isPrivate()).thenReturn(false);
register.put("priv.Type", privateType);
register.put("pub.Type", publicType);
List<String> result = register.getPublicVrTypes();
assertEquals(1, result.size());
assertEquals("pub.Type", result.get(0));
}

@Test(expected = GateException.class)
public void testRegisterPluginThrowsWhenPluginIsInvalid() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
// when(plugin.getRequiredPlugins()).thenReturn(Collections.emptyList());
when(plugin.isValid()).thenReturn(false);
when(plugin.getName()).thenReturn("BadPlugin");
when(plugin.getVersion()).thenReturn("1.0");
register.registerPlugin(plugin);
}

@Test(expected = GateException.class)
public void testRegisterDirectoriesThrowsOnPluginException() throws Exception {
CreoleRegisterImpl register = spy(new CreoleRegisterImpl());
URL pluginURL = new URL("http://invalid/plugin/");
doThrow(new GateException("Plugin error")).when(register).registerPlugin(any(Plugin.class));
register.registerDirectories(pluginURL);
}

@Test
public void testRemoveDirectorySkipsNullURL() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
register.removeDirectory(null);
}

@Test
public void testRemoveDirectoryUnregistersMatchingPlugin() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
URL mockUrl = new URL("http://abc/plugin/");
when(plugin.getBaseURL()).thenReturn(mockUrl);
// when(plugin.getRequiredPlugins()).thenReturn(Collections.emptyList());
when(plugin.getName()).thenReturn("TestPlugin");
when(plugin.getVersion()).thenReturn("1.0");
// when(plugin.getCreoleXML()).thenReturn(new Document(new Element("CREOLE")));
when(plugin.getResourceInfoList()).thenReturn(Collections.emptyList());
when(plugin.isValid()).thenReturn(true);
register.registerPlugin(plugin);
assertTrue(register.getPlugins().contains(plugin));
register.removeDirectory(mockUrl);
assertFalse(register.getPlugins().contains(plugin));
}

@Test
public void testRemoveReturnsNullWhenKeyNotFound() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData result = register.remove("non.existent.Type");
assertNull(result);
}

@Test
public void testGetPublicTypesReturnsEmptyIfAllPrivate() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData privateData = mock(ResourceData.class);
when(privateData.getClassName()).thenReturn("hidden.Type");
// when(privateData.getResourceClass()).thenReturn(MyLanguageResource.class);
when(privateData.isPrivate()).thenReturn(true);
register.put("hidden.Type", privateData);
List<String> types = register.getPublicLrTypes();
assertTrue(types.isEmpty());
}

@Test
public void testRegisterBuiltinsHandlesExceptionGracefully() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Gate.setBuiltinCreoleDir(new URL("file:/nonexistent/"));
try {
register.registerBuiltins();
fail("Expected GateException");
} catch (GateException e) {
}
}

@Test
public void testGetLargeVRsForResourceHandlesNullInput() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<String> results = register.getLargeVRsForResource(null);
assertNotNull(results);
assertTrue(results.isEmpty());
}

@Test
public void testGetSmallVRsForResourceHandlesNullInput() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<String> results = register.getSmallVRsForResource(null);
assertNotNull(results);
assertTrue(results.isEmpty());
}

@Test
public void testGetAnnotationVRsForTypeReturnsEmptyOnNullInput() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<String> results = register.getAnnotationVRs(null);
assertNotNull(results);
assertTrue(results.isEmpty());
}

@Test
public void testGetAllInstancesIncludesOnlyMatchingTypesAndNonHidden() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = mock(ResourceData.class);
when(data.getClassName()).thenReturn("gate.test.Visible");
// when(data.getResourceClass()).thenReturn(MyLanguageResource.class);
Resource instance = mock(Resource.class);
FeatureMap features = Factory.newFeatureMap();
when(instance.getFeatures()).thenReturn(features);
List<Resource> instances = new ArrayList<Resource>();
instances.add(instance);
when(data.getInstantiations()).thenReturn(instances);
register.put("gate.test.Visible", data);
List<Resource> all = register.getAllInstances("gate.creole.CreoleRegisterImplTest$MyLanguageResource");
assertEquals(1, all.size());
assertEquals(instance, all.get(0));
}

@Test(expected = GateException.class)
public void testGetAllInstancesThrowsIfClassNotFound() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
register.getAllInstances("non.existing.Type");
}

@Test
public void testFireResourceUnloadedCallsListenerMethod() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleListener listener = mock(CreoleListener.class);
register.addCreoleListener(listener);
CreoleEvent event = mock(CreoleEvent.class);
register.resourceUnloaded(event);
verify(listener).resourceUnloaded(event);
}

@Test
public void testFireDatastoreEvents() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleListener listener = mock(CreoleListener.class);
register.addCreoleListener(listener);
CreoleEvent event = mock(CreoleEvent.class);
register.datastoreCreated(event);
register.datastoreOpened(event);
register.datastoreClosed(event);
verify(listener).datastoreCreated(event);
verify(listener).datastoreOpened(event);
verify(listener).datastoreClosed(event);
}

@Test(expected = GateException.class)
public void testParseDirectoryThrowsOnURISyntaxException() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
Document doc = mock(Document.class);
URL dirUrl = new URL("http://invalid-dir");
URL creoleUrl = new URL("http://invalid-dir/creole.xml");
Plugin brokenPlugin = mock(Plugin.class);
when(brokenPlugin.getBaseURI()).thenReturn(null);
when(brokenPlugin.getBaseURL()).thenReturn(dirUrl);
register.registerPlugin(brokenPlugin);
}

@Test
public void testGetVRsForResourceWithDuplicateDefaultVR() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData vr1 = mock(ResourceData.class);
// when(vr1.getResourceClass()).thenReturn(MyVisualResource.class);
when(vr1.getGuiType()).thenReturn(ResourceData.LARGE_GUI);
when(vr1.getResourceDisplayed()).thenReturn("foo.Type");
when(vr1.isMainView()).thenReturn(true);
when(vr1.getClassName()).thenReturn("vr.Type.Main");
ResourceData vr2 = mock(ResourceData.class);
// when(vr2.getResourceClass()).thenReturn(MyVisualResource.class);
when(vr2.getGuiType()).thenReturn(ResourceData.LARGE_GUI);
when(vr2.getResourceDisplayed()).thenReturn("foo.Type");
when(vr2.isMainView()).thenReturn(false);
when(vr2.getClassName()).thenReturn("vr.Type.Secondary");
when(vr1.getAnnotationTypeDisplayed()).thenReturn(null);
when(vr2.getAnnotationTypeDisplayed()).thenReturn(null);
register.put("vr.Type.Main", vr1);
register.put("vr.Type.Secondary", vr2);
// Class<?> targetClass = MyLanguageResource.class;
// String targetClassName = targetClass.getName();
// register.getVRsForResource(targetClassName, ResourceData.LARGE_GUI);
// List<String> result = register.getLargeVRsForResource(targetClassName);
// assertEquals(2, result.size());
// assertEquals("vr.Type.Main", result.get(0));
// assertEquals("vr.Type.Secondary", result.get(1));
}

@Test
public void testGetAnnotationVRsReturnsMultipleMatchesAndRespectsDefault() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData vrA = mock(ResourceData.class);
ResourceData vrB = mock(ResourceData.class);
// Class<?> cls = MyVisualResource.class;
when(vrA.getClassName()).thenReturn("AVR.Main");
when(vrA.getGuiType()).thenReturn(ResourceData.NULL_GUI);
when(vrA.getAnnotationTypeDisplayed()).thenReturn("Person");
when(vrA.getResourceDisplayed()).thenReturn(null);
// when(vrA.getResourceClass()).thenReturn(cls);
when(vrA.isMainView()).thenReturn(true);
when(vrB.getClassName()).thenReturn("AVR.Second");
when(vrB.getGuiType()).thenReturn(ResourceData.NULL_GUI);
when(vrB.getAnnotationTypeDisplayed()).thenReturn("Person");
when(vrB.getResourceDisplayed()).thenReturn(null);
// when(vrB.getResourceClass()).thenReturn(cls);
when(vrB.isMainView()).thenReturn(false);
register.put("AVR.Main", vrA);
register.put("AVR.Second", vrB);
List<String> result = register.getAnnotationVRs("Person");
assertEquals(2, result.size());
assertEquals("AVR.Main", result.get(0));
assertEquals("AVR.Second", result.get(1));
}

@Test
public void testGetPublicsReturnsEmptyWhenAllPrivate() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData privateRD = mock(ResourceData.class);
when(privateRD.getClassName()).thenReturn("x.Priv");
// when(privateRD.getResourceClass()).thenReturn(MyLanguageResource.class);
when(privateRD.isPrivate()).thenReturn(true);
Resource resourceInstance = mock(Resource.class);
// when(resourceInstance.getClass()).thenReturn(MyLanguageResource.class);
List<Resource> list = new ArrayList<Resource>();
list.add(resourceInstance);
when(privateRD.getInstantiations()).thenReturn(list);
register.put("x.Priv", privateRD);
List<LanguageResource> lrs = register.getPublicLrInstances();
assertTrue(lrs.isEmpty());
}

@Test(expected = GateRuntimeException.class)
public void testRemoveThrowsWhenClassCannotBeLoaded() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn("gate.FakeType");
when(rd.reduceReferenceCount()).thenReturn(0);
when(rd.getResourceClass()).thenThrow(new ClassNotFoundException("expected"));
register.put("gate.FakeType", rd);
register.remove("gate.FakeType");
}

@Test
public void testSetResourceNameWithNoListenersDoesNotThrow() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Resource res = mock(Resource.class);
when(res.getName()).thenReturn("old");
register.setResourceName(res, "new");
verify(res).setName("new");
}

@Test
public void testRegisterPluginNestedDependencyLoadsAll() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin dep1 = mock(Plugin.class);
Plugin dep2 = mock(Plugin.class);
Plugin parent = mock(Plugin.class);
// when(dep1.getRequiredPlugins()).thenReturn(Collections.emptyList());
// when(dep2.getRequiredPlugins()).thenReturn(Collections.singletonList(dep1));
// when(parent.getRequiredPlugins()).thenReturn(Collections.singletonList(dep2));
// when(dep1.getCreoleXML()).thenReturn(new Document(new Element("CREOLE")));
// when(dep2.getCreoleXML()).thenReturn(new Document(new Element("CREOLE")));
// when(parent.getCreoleXML()).thenReturn(new Document(new Element("CREOLE")));
when(dep1.isValid()).thenReturn(true);
when(dep2.isValid()).thenReturn(true);
when(parent.isValid()).thenReturn(true);
when(dep1.getBaseURL()).thenReturn(new URL("http://gate/plugin/dep1/"));
when(dep2.getBaseURL()).thenReturn(new URL("http://gate/plugin/dep2/"));
when(parent.getBaseURL()).thenReturn(new URL("http://gate/plugin/parent/"));
when(dep1.getName()).thenReturn("Dep1");
when(dep1.getVersion()).thenReturn("1.0");
when(dep1.getResourceInfoList()).thenReturn(Collections.emptyList());
when(dep2.getName()).thenReturn("Dep2");
when(dep2.getVersion()).thenReturn("1.0");
when(dep2.getResourceInfoList()).thenReturn(Collections.emptyList());
when(parent.getName()).thenReturn("Parent");
when(parent.getVersion()).thenReturn("1.0");
when(parent.getResourceInfoList()).thenReturn(Collections.emptyList());
register.registerPlugin(parent);
Set<Plugin> loaded = register.getPlugins();
assertTrue(loaded.contains(dep1));
assertTrue(loaded.contains(dep2));
assertTrue(loaded.contains(parent));
}

@Test
public void testPutResourceDataThatIsPRandVR() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn("gate.test.MixedResource");
// when(rd.getResourceClass()).thenReturn(MixedPRVRResource.class);
when(rd.isTool()).thenReturn(false);
ResourceData inserted = register.put("gate.test.MixedResource", rd);
assertNull(inserted);
assertTrue(register.getPrTypes().contains("gate.test.MixedResource"));
assertTrue(register.getVrTypes().contains("gate.test.MixedResource"));
}

@Test(expected = GateException.class)
public void testRegisterComponentThrowsGateExceptionOnMalformedURL() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
// Class<? extends Resource> badClass = new Resource() {
// 
// @Override
// public void cleanup() {
// }
// 
// @Override
// public void setName(String name) {
// }
// 
// @Override
// public String getName() {
// return "x";
// }
// 
// @Override
// public FeatureMap getFeatures() {
// return null;
// }
// 
// @Override
// public Resource init() {
// return this;
// }
// 
// @Override
// public Resource init(FeatureMap parameters) {
// return this;
// }
// 
// @Override
// public ResourceData getResourceData() {
// return null;
// }
// }.getClass();
// register.registerComponent(badClass);
}

@Test
public void testGetPublicControllerTypesOmitsPrivateEntries() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData privateCtrl = mock(ResourceData.class);
when(privateCtrl.getClassName()).thenReturn("gate.mock.PrivateCtrl");
// when(privateCtrl.getResourceClass()).thenReturn(DummyController.class);
when(privateCtrl.isPrivate()).thenReturn(true);
ResourceData publicCtrl = mock(ResourceData.class);
when(publicCtrl.getClassName()).thenReturn("gate.mock.PublicCtrl");
// when(publicCtrl.getResourceClass()).thenReturn(DummyController.class);
when(publicCtrl.isPrivate()).thenReturn(false);
register.put("gate.mock.PrivateCtrl", privateCtrl);
register.put("gate.mock.PublicCtrl", publicCtrl);
List<String> result = register.getPublicControllerTypes();
assertEquals(1, result.size());
assertTrue(result.contains("gate.mock.PublicCtrl"));
}

@Test
public void testGetAnnotationVRsIgnoresMismatchedAnnotationTypes() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn("gate.vr.TestAVR");
// when(rd.getResourceClass()).thenReturn(MyVisualResource.class);
when(rd.getAnnotationTypeDisplayed()).thenReturn("Organization");
when(rd.getGuiType()).thenReturn(ResourceData.NULL_GUI);
when(rd.getResourceDisplayed()).thenReturn(null);
register.put("gate.vr.TestAVR", rd);
List<String> vrList = register.getAnnotationVRs("Person");
assertTrue(vrList.isEmpty());
}

@Test
public void testGetVRsForResourceReturnsEmptyIfNoMatch() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn("gate.vr.NoMatchVR");
when(rd.getResourceDisplayed()).thenReturn("SomeOtherType");
when(rd.getGuiType()).thenReturn(ResourceData.LARGE_GUI);
// when(rd.getResourceClass()).thenReturn(MyVisualResource.class);
register.put("gate.vr.NoMatchVR", rd);
List<String> result = register.getLargeVRsForResource("gate.FooBar");
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testPutSameKeyReturnsExistingResourceData() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn("gate.test.DupType");
// when(rd.getResourceClass()).thenReturn(MyLanguageResource.class);
when(rd.increaseReferenceCount()).thenReturn(2);
ResourceData first = register.put("gate.test.DupType", rd);
ResourceData second = register.put("gate.test.DupType", rd);
assertNull(first);
assertEquals(rd, second);
verify(rd).increaseReferenceCount();
}

@Test
public void testUnregisterPluginRemovesDependentPluginRecursively() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin base = mock(Plugin.class);
when(base.getName()).thenReturn("BasePlugin");
when(base.getVersion()).thenReturn("1.0");
when(base.getBaseURL()).thenReturn(new URL("http://gate/plugin/base"));
// when(base.getRequiredPlugins()).thenReturn(Collections.emptyList());
// when(base.getCreoleXML()).thenReturn(new Document(new Element("CREOLE")));
when(base.getResourceInfoList()).thenReturn(Collections.emptyList());
when(base.isValid()).thenReturn(true);
Plugin child = mock(Plugin.class);
when(child.getName()).thenReturn("ChildPlugin");
when(child.getVersion()).thenReturn("1.0");
when(child.getBaseURL()).thenReturn(new URL("http://gate/plugin/child"));
// when(child.getRequiredPlugins()).thenReturn(Collections.singletonList(base));
// when(child.getCreoleXML()).thenReturn(new Document(new Element("CREOLE")));
when(child.getResourceInfoList()).thenReturn(Collections.emptyList());
when(child.isValid()).thenReturn(true);
register.registerPlugin(base);
register.registerPlugin(child);
assertTrue(register.getPlugins().contains(child));
assertTrue(register.getPlugins().contains(base));
register.unregisterPlugin(base);
assertFalse(register.getPlugins().contains(child));
assertFalse(register.getPlugins().contains(base));
}

@Test
public void testClearUnloadsAllPluginsAndResources() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn("gate.test.ToBeCleared");
// when(rd.getResourceClass()).thenReturn(MyLanguageResource.class);
when(rd.isTool()).thenReturn(true);
register.put("gate.test.ToBeCleared", rd);
Plugin plugin = mock(Plugin.class);
when(plugin.getName()).thenReturn("ClearablePlugin");
when(plugin.getVersion()).thenReturn("1.0");
when(plugin.getBaseURL()).thenReturn(new URL("http://gate/plugin/toClear"));
// when(plugin.getRequiredPlugins()).thenReturn(Collections.emptyList());
// when(plugin.getCreoleXML()).thenReturn(new Document(new Element("CREOLE")));
when(plugin.isValid()).thenReturn(true);
when(plugin.getResourceInfoList()).thenReturn(Collections.emptyList());
register.registerPlugin(plugin);
assertFalse(register.getPrTypes().isEmpty());
assertFalse(register.getToolTypes().isEmpty());
assertFalse(register.getPlugins().isEmpty());
register.clear();
assertTrue(register.getPlugins().isEmpty());
assertTrue(register.getToolTypes().isEmpty());
assertTrue(register.getPrTypes().isEmpty());
assertTrue(register.getLrTypes().isEmpty());
}

@Test
public void testGetControllerTypesReturnsUnmodifiableSet() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn("gate.test.ControllerStub");
// when(rd.getResourceClass()).thenReturn(DummyController.class);
when(rd.isTool()).thenReturn(false);
register.put("gate.test.ControllerStub", rd);
Set<String> controllerTypes = register.getControllerTypes();
try {
controllerTypes.add("test.Invalid");
fail("Should throw UnsupportedOperationException");
} catch (UnsupportedOperationException e) {
}
}

@Test
public void testGetLargeVRsForResourceWhenTypeDoesNotMatch() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData vr = mock(ResourceData.class);
when(vr.getResourceDisplayed()).thenReturn("other.Type");
when(vr.getGuiType()).thenReturn(ResourceData.LARGE_GUI);
when(vr.getClassName()).thenReturn("vr.other.VR");
// when(vr.getResourceClass()).thenReturn(MyVisualResource.class);
register.put("vr.other.VR", vr);
List<String> result = register.getLargeVRsForResource("non.matching.Type");
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testRedundantPutWithNewObjectDifferentInstanceIgnored() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd1 = mock(ResourceData.class);
when(rd1.getClassName()).thenReturn("gate.test.Duplicate");
// when(rd1.getResourceClass()).thenReturn(MyLanguageResource.class);
when(rd1.isTool()).thenReturn(false);
when(rd1.increaseReferenceCount()).thenReturn(2);
ResourceData rd2 = mock(ResourceData.class);
when(rd2.getClassName()).thenReturn("gate.test.Duplicate");
// when(rd2.getResourceClass()).thenReturn(MyLanguageResource.class);
when(rd2.isTool()).thenReturn(false);
when(rd2.increaseReferenceCount()).thenReturn(2);
ResourceData put1 = register.put("gate.test.Duplicate", rd1);
ResourceData put2 = register.put("gate.test.Duplicate", rd2);
assertNull(put1);
assertEquals(rd1, put2);
verify(rd2).increaseReferenceCount();
}

@Test
public void testGetAllInstancesWithAssignableTypesReturnsOnlyCorrectOnes() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = mock(ResourceData.class);
when(data.getClassName()).thenReturn("gate.MockX");
// when(data.getResourceClass()).thenReturn(Object.class);
Resource inst = mock(Resource.class);
FeatureMap fm = Factory.newFeatureMap();
when(inst.getFeatures()).thenReturn(fm);
List<Resource> instances = new ArrayList<Resource>();
instances.add(inst);
when(data.getInstantiations()).thenReturn(instances);
register.put("gate.MockX", data);
List<Resource> result = register.getAllInstances("gate.creole.CreoleRegisterImplTest$MyLanguageResource");
assertTrue(result.isEmpty());
}

@Test
public void testGetTypedResourceListCastsOnlyMatchingInstances() throws Exception {
Resource expected = mock(LanguageResource.class);
Resource mismatch = mock(Resource.class);
FeatureMap fm = Factory.newFeatureMap();
when(expected.getFeatures()).thenReturn(fm);
when(mismatch.getFeatures()).thenReturn(fm);
List<Resource> rawList = new ArrayList<Resource>();
rawList.add(expected);
rawList.add(mismatch);
// CreoleRegisterImpl.TypedResourceList<LanguageResource> typedList = new CreoleRegisterImpl.TypedResourceList<LanguageResource>(rawList, LanguageResource.class);
// LanguageResource res0 = typedList.get(0);
// assertEquals(expected, res0);
try {
// typedList.get(1);
fail("Expected ClassCastException");
} catch (ClassCastException e) {
}
}

@Test(expected = GateException.class)
public void testRegisterPluginThrowsOnCreoleXmlException() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
// when(plugin.getRequiredPlugins()).thenReturn(Collections.emptyList());
when(plugin.getCreoleXML()).thenThrow(new RuntimeException("malformed XML"));
when(plugin.isValid()).thenReturn(true);
when(plugin.getBaseURL()).thenReturn(new URL("http://plugins/base"));
when(plugin.getName()).thenReturn("BadPlugin");
when(plugin.getVersion()).thenReturn("1.0");
register.registerPlugin(plugin);
}

@Test
public void testResourceRenamedToSameNameFiresEventWithDifferentOldAndNewName() {
// CreoleRegisterImpl register = new CreoleRegisterImpl();
Resource res = mock(Resource.class);
when(res.getName()).thenReturn("example");
CreoleListener listener = mock(CreoleListener.class);
// register.addCreoleListener(listener);
// register.setResourceName(res, "example");
verify(listener).resourceRenamed(res, "example", "example");
}

@Test
public void testGetLrInstancesTypeExistsButHasNoInstancesReturnsEmptyList() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn("test.LRZ");
// when(rd.getResourceClass()).thenReturn(MyLanguageResource.class);
when(rd.getInstantiations()).thenReturn(Collections.<Resource>emptyList());
register.put("test.LRZ", rd);
List<LanguageResource> instances = register.getLrInstances("test.LRZ");
assertNotNull(instances);
assertTrue(instances.isEmpty());
}

@Test
public void testGetPluginSetImmutableAfterRegistration() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
when(plugin.getBaseURL()).thenReturn(new URL("http://example.com/x"));
// when(plugin.getCreoleXML()).thenReturn(new Document(new Element("CREOLE")));
when(plugin.isValid()).thenReturn(true);
when(plugin.getName()).thenReturn("TestPlugin");
when(plugin.getVersion()).thenReturn("1.0");
// when(plugin.getRequiredPlugins()).thenReturn(Collections.emptyList());
when(plugin.getResourceInfoList()).thenReturn(Collections.emptyList());
register.registerPlugin(plugin);
Set<Plugin> plugins = register.getPlugins();
try {
plugins.clear();
fail("Expected UnsupportedOperationException");
} catch (UnsupportedOperationException e) {
}
assertTrue(plugins.contains(plugin));
}

@Test
public void testRemoveObjectReturnsNullWhenKeyIsNull() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData result = register.remove(null);
assertNull(result);
}

@Test
public void testRemoveDoesNotTouchTypesWhenReferenceCountGreaterThanZero() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn("gate.test.SomePR");
// when(rd.getResourceClass()).thenReturn(MyProcessingResource.class);
when(rd.reduceReferenceCount()).thenReturn(1);
register.put("gate.test.SomePR", rd);
register.remove("gate.test.SomePR");
assertTrue(register.getPrTypes().contains("gate.test.SomePR"));
}

@Test
public void testPutHandlesNullToolTypesGracefully() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn("gate.test.Tool");
// when(rd.getResourceClass()).thenReturn(MyProcessingResource.class);
when(rd.isTool()).thenReturn(true);
register.toolTypes = null;
register.put("gate.test.Tool", rd);
assertTrue(register.getToolTypes().contains("gate.test.Tool"));
}

@Test
public void testFireMethodsDoNothingWhenListenersAreNull() {
// CreoleRegisterImpl register = new CreoleRegisterImpl();
// register.creoleListeners = null;
CreoleEvent event = mock(CreoleEvent.class);
Resource res = mock(Resource.class);
// register.resourceLoaded(event);
// register.resourceUnloaded(event);
// register.resourceRenamed(res, "a", "b");
// register.datastoreCreated(event);
// register.datastoreClosed(event);
// register.datastoreOpened(event);
}

@Test
public void testGetAllInstancesReturnsEmptyWhenNoneMatch() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = mock(ResourceData.class);
when(data.getClassName()).thenReturn("test.Other");
// when(data.getResourceClass()).thenReturn(Object.class);
when(data.getInstantiations()).thenReturn(Collections.emptyList());
register.put("test.Other", data);
List<Resource> result = register.getAllInstances("gate.creole.Resource");
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test(expected = GateRuntimeException.class)
public void testPutThrowsWhenGetResourceClassFails() {
// CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData badData = mock(ResourceData.class);
when(badData.getClassName()).thenReturn("gate.test.Unknown");
try {
when(badData.getResourceClass()).thenThrow(new ClassNotFoundException());
} catch (ClassNotFoundException e) {
}
// register.put("gate.test.Unknown", badData);
}

@Test
public void testPutHandlesNullVrTypesOnDeserializationScenario() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn("gate.test.VR");
// when(rd.getResourceClass()).thenReturn(MyVisualResource.class);
when(rd.isTool()).thenReturn(false);
register.vrTypes = null;
register.put("gate.test.VR", rd);
// List<String> vrTypes = register.getVrTypes();
// assertTrue(vrTypes.contains("gate.test.VR"));
}

@Test
public void testGetPublicVrTypesSkipsNullResourceData() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
register.put("gate.test.NullVR", null);
List<String> result = register.getPublicVrTypes();
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetAnnotationVRsReturnsDefaultFirstWhenMultipleMatches() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData mainVR = mock(ResourceData.class);
when(mainVR.getClassName()).thenReturn("gate.avr.Main");
when(mainVR.getGuiType()).thenReturn(ResourceData.NULL_GUI);
when(mainVR.getAnnotationTypeDisplayed()).thenReturn("Token");
when(mainVR.getResourceDisplayed()).thenReturn(null);
// when(mainVR.getResourceClass()).thenReturn(MyVisualResource.class);
when(mainVR.isMainView()).thenReturn(true);
ResourceData secondaryVR = mock(ResourceData.class);
when(secondaryVR.getClassName()).thenReturn("gate.avr.Secondary");
when(secondaryVR.getGuiType()).thenReturn(ResourceData.NULL_GUI);
when(secondaryVR.getAnnotationTypeDisplayed()).thenReturn("Token");
when(secondaryVR.getResourceDisplayed()).thenReturn(null);
// when(secondaryVR.getResourceClass()).thenReturn(MyVisualResource.class);
when(secondaryVR.isMainView()).thenReturn(false);
register.put("gate.avr.Main", mainVR);
register.put("gate.avr.Secondary", secondaryVR);
List<String> result = register.getAnnotationVRs("Token");
assertEquals(2, result.size());
assertEquals("gate.avr.Main", result.get(0));
assertEquals("gate.avr.Secondary", result.get(1));
}

@Test
public void testPluginNamesMappingHandlesMalformedURL() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Gate.setBuiltinCreoleDir(new URL("file:/non/existing/invalid/"));
register.pluginNamesMappings = null;
register.getDirectories();
}

@Test
public void testGetTypedResourceListReturnsCorrectSize() throws Exception {
Resource r1 = mock(LanguageResource.class);
Resource r2 = mock(LanguageResource.class);
List<Resource> backing = Arrays.asList(r1, r2);
// CreoleRegisterImpl.TypedResourceList<LanguageResource> list = new CreoleRegisterImpl.TypedResourceList<LanguageResource>(backing, LanguageResource.class);
// assertEquals(2, list.size());
// assertSame(r1, list.get(0));
// assertSame(r2, list.get(1));
}

@Test
public void testUnregisterPluginSkipsUnmatchedPluginURL() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
when(plugin.getBaseURL()).thenReturn(new URL("http://gate.ac.uk/plugin/a"));
// when(plugin.getRequiredPlugins()).thenReturn(Collections.emptyList());
// when(plugin.getCreoleXML()).thenReturn(new Document(new Element("CREOLE")));
when(plugin.getResourceInfoList()).thenReturn(Collections.emptyList());
when(plugin.isValid()).thenReturn(true);
when(plugin.getName()).thenReturn("PluginA");
when(plugin.getVersion()).thenReturn("1.0");
register.registerPlugin(plugin);
URL nonMatchingURL = new URL("http://gate.ac.uk/plugin/b");
register.removeDirectory(nonMatchingURL);
assertTrue(register.getPlugins().contains(plugin));
}

@Test
public void testGetPublicTypesIgnoresNullResourceData() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
register.put("gate.null.Type", null);
Collection<String> inputTypes = Arrays.asList("gate.null.Type");
List<String> result = register.getPublicTypes(inputTypes);
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetVrInstancesForUnregisteredTypeReturnsEmptyList() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<VisualResource> list = register.getVrInstances("nonexistent.Type");
assertNotNull(list);
assertEquals(0, list.size());
}

@Test
public void testGetVrInstancesReturnsOnlyMatchingTypeInstances() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = mock(ResourceData.class);
Resource instance1 = mock(VisualResource.class);
FeatureMap features = Factory.newFeatureMap();
when(instance1.getFeatures()).thenReturn(features);
List<Resource> instances = new ArrayList<Resource>();
instances.add(instance1);
when(data.getClassName()).thenReturn("gate.vr.Mock");
// when(data.getResourceClass()).thenReturn(MyVisualResource.class);
when(data.getInstantiations()).thenReturn(instances);
register.put("gate.vr.Mock", data);
List<VisualResource> vrList = register.getVrInstances("gate.vr.Mock");
assertEquals(1, vrList.size());
assertEquals(instance1, vrList.get(0));
}

@Test
public void testGetPublicsReturnsUnmodifiableList() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
Resource resource = mock(Resource.class);
when(rd.getClassName()).thenReturn("test.Mock");
// when(rd.getResourceClass()).thenReturn(MyLanguageResource.class);
when(rd.isPrivate()).thenReturn(false);
// when(resource.getClass()).thenReturn(MyLanguageResource.class);
when(rd.getInstantiations()).thenReturn(Collections.singletonList(resource));
register.put("test.Mock", rd);
List<LanguageResource> list = register.getPublicLrInstances();
try {
list.add(mock(LanguageResource.class));
fail("Expected UnsupportedOperationException");
} catch (UnsupportedOperationException e) {
}
}

@Test
public void testParseDirectoryWithEmptyXMLDoesNotThrow() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
when(plugin.getName()).thenReturn("EmptyPlugin");
when(plugin.getVersion()).thenReturn("1.0");
when(plugin.getBaseURL()).thenReturn(new URL("http://gate.ac.uk/plugin/empty"));
when(plugin.getBaseURI()).thenReturn(new URL("http://gate.ac.uk/plugin/empty").toURI());
// when(plugin.getCreoleXML()).thenReturn(new Document(new Element("CREOLE")));
// Document xml = plugin.getCreoleXML();
URL dirURL = plugin.getBaseURL();
URL creoleURL = new URL(dirURL.toString() + "/creole.xml");
// register.parseDirectory(plugin, xml, dirURL, creoleURL);
}

@Test
public void testRemoveResourceWithControllerRemovesFromControllerTypes() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = mock(ResourceData.class);
when(rd.getClassName()).thenReturn("gate.ctrl.MyController");
// when(rd.getResourceClass()).thenReturn(DummyController.class);
when(rd.reduceReferenceCount()).thenReturn(0);
when(rd.isTool()).thenReturn(false);
register.put("gate.ctrl.MyController", rd);
ResourceData removed = register.remove("gate.ctrl.MyController");
assertNotNull(removed);
assertFalse(register.getControllerTypes().contains("gate.ctrl.MyController"));
}

@Test
public void testAddCreoleListenerIdempotent() {
// CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleListener listener = mock(CreoleListener.class);
// register.addCreoleListener(listener);
// register.addCreoleListener(listener);
// register.resourceLoaded(mock(CreoleEvent.class));
verify(listener, times(1)).resourceLoaded(any(CreoleEvent.class));
}

@Test
public void testRemoveCreoleListenerSilentlyIgnoresUnknownListener() {
// CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleListener listener1 = mock(CreoleListener.class);
CreoleListener listener2 = mock(CreoleListener.class);
// register.addCreoleListener(listener1);
// register.removeCreoleListener(listener2);
// register.resourceLoaded(mock(CreoleEvent.class));
verify(listener1, times(1)).resourceLoaded(any(CreoleEvent.class));
verify(listener2, never()).resourceLoaded(any(CreoleEvent.class));
}

@Test
public void testPutAddsVisualResourceOnlyOnceIfAlreadyPresentInList() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
register.vrTypes = new ArrayList<String>();
ResourceData vrData = mock(ResourceData.class);
when(vrData.getClassName()).thenReturn("gate.vr.DuplicateVR");
// when(vrData.getResourceClass()).thenReturn(MyVisualResource.class);
when(vrData.isTool()).thenReturn(false);
register.vrTypes.add("gate.vr.DuplicateVR");
ResourceData returned = register.put("gate.vr.DuplicateVR", vrData);
assertNull(register.vrTypes.subList(1, register.vrTypes.size()).stream().filter(s -> s.equals("gate.vr.DuplicateVR")).findAny().orElse(null));
}
}

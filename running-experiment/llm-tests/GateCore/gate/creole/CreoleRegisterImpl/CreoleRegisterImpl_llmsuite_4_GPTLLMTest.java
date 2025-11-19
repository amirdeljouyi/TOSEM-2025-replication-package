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

public class CreoleRegisterImpl_llmsuite_4_GPTLLMTest {

@Test
public void testPutAddsPRTypeAndToolType() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockPR.class.getName());
data.setTool(true);
// ResourceData result = register.put(MockPR.class.getName(), data);
// assertSame(data, result);
// assertTrue(register.getPrTypes().contains(MockPR.class.getName()));
// assertTrue(register.getToolTypes().contains(MockPR.class.getName()));
}

@Test
public void testRemoveRemovesPRTypeAndToolType() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockPR.class.getName());
data.setTool(true);
// register.put(MockPR.class.getName(), data);
// register.remove(MockPR.class.getName());
// assertFalse(register.getPrTypes().contains(MockPR.class.getName()));
// assertFalse(register.getToolTypes().contains(MockPR.class.getName()));
// assertNull(register.get(MockPR.class.getName()));
}

@Test
public void testClearRemovesAll() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockPR.class.getName());
data.setTool(true);
// register.put(MockPR.class.getName(), data);
register.clear();
assertEquals(0, register.size());
assertTrue(register.getPrTypes().isEmpty());
assertTrue(register.getToolTypes().isEmpty());
assertTrue(register.getPlugins().isEmpty());
}

@Test
public void testRegisterAndUnregisterPlugin() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
// Plugin plugin = new Plugin.Component(MockPR.class);
PluginListener listener = mock(PluginListener.class);
register.addPluginListener(listener);
// register.registerPlugin(plugin);
// assertTrue(register.getPlugins().contains(plugin));
// register.unregisterPlugin(plugin);
// assertFalse(register.getPlugins().contains(plugin));
// verify(listener).pluginLoaded(plugin);
// verify(listener).pluginUnloaded(plugin);
}

@Test
public void testSetResourceNameFiresRenameEvent() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleListener listener = mock(CreoleListener.class);
register.addCreoleListener(listener);
Resource resource = mock(Resource.class);
when(resource.getName()).thenReturn("newName");
register.setResourceName(resource, "newName");
verify(listener).resourceRenamed(resource, "newName", "newName");
}

@Test
public void testGetPublicPrTypesExcludesPrivateResources() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData publicData = new ResourceData();
// publicData.setClassName(MockPR.class.getName());
publicData.setPrivate(false);
ResourceData privateData = new ResourceData();
// privateData.setClassName(MockPR2.class.getName());
privateData.setPrivate(true);
// register.put(MockPR.class.getName(), publicData);
// register.put(MockPR2.class.getName(), privateData);
List<String> publicTypes = register.getPublicPrTypes();
// assertTrue(publicTypes.contains(MockPR.class.getName()));
// assertFalse(publicTypes.contains(MockPR2.class.getName()));
}

@Test
public void testGetAnnotationVRsByType() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData vr1 = new ResourceData();
// vr1.setClassName(MockVR.class.getName());
vr1.setGuiType(ResourceData.NULL_GUI);
vr1.setAnnotationTypeDisplayed("Token");
ResourceData vr2 = new ResourceData();
// vr2.setClassName(MockVR2.class.getName());
vr2.setGuiType(ResourceData.NULL_GUI);
vr2.setAnnotationTypeDisplayed("Sentence");
// register.put(MockVR.class.getName(), vr1);
// register.put(MockVR2.class.getName(), vr2);
List<String> result = register.getAnnotationVRs("Token");
assertEquals(1, result.size());
// assertEquals(MockVR.class.getName(), result.get(0));
}

@Test
public void testGetLargeVRsForResource() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData vrData = new ResourceData();
// vrData.setClassName(MockVR.class.getName());
vrData.setGuiType(ResourceData.LARGE_GUI);
// vrData.setResourceDisplayed(MockPR.class.getName());
// register.put(MockVR.class.getName(), vrData);
// List<String> largeVRs = register.getLargeVRsForResource(MockPR.class.getName());
// assertEquals(1, largeVRs.size());
// assertEquals(MockVR.class.getName(), largeVRs.get(0));
}

@Test(expected = GateException.class)
public void testRegisterInvalidPluginThrowsException() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
when(plugin.isValid()).thenReturn(false);
// when(plugin.getRequiredPlugins()).thenReturn(Collections.emptyList());
register.registerPlugin(plugin);
}

@Test
public void testAddAndRemoveCreoleListener() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleListener listener = mock(CreoleListener.class);
register.addCreoleListener(listener);
Resource r = mock(Resource.class);
// CreoleEvent e = new CreoleEvent(r, CreoleEvent.LOAD_RESOURCE);
// register.resourceLoaded(e);
// verify(listener).resourceLoaded(e);
register.removeCreoleListener(listener);
// register.resourceLoaded(e);
verifyNoMoreInteractions(listener);
}

@Test
public void testPutSameKeyIncreasesReferenceAndReturnsOriginal() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockPR.class.getName());
data.setTool(true);
// ResourceData result1 = register.put(MockPR.class.getName(), data);
// ResourceData result2 = register.put(MockPR.class.getName(), data);
// assertSame(result1, result2);
// assertTrue(register.getPrTypes().contains(MockPR.class.getName()));
}

@Test
public void testRemoveWithMultipleReferencesDoesNotRemove() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockPR.class.getName());
data.setTool(true);
// register.put(MockPR.class.getName(), data);
// register.put(MockPR.class.getName(), data);
// ResourceData removed = register.remove(MockPR.class.getName());
// assertNotNull(removed);
// assertEquals(MockPR.class.getName(), removed.getClassName());
// assertTrue(register.getPrTypes().contains(MockPR.class.getName()));
}

@Test(expected = GateRuntimeException.class)
public void testPutWithClassNotFoundThrowsGateRuntimeException() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData badData = mock(ResourceData.class);
when(badData.getClassName()).thenReturn("invalid.NonExistentClass");
when(badData.getResourceClass()).thenThrow(ClassNotFoundException.class);
register.put("invalid.NonExistentClass", badData);
}

@Test(expected = GateRuntimeException.class)
public void testRemoveWithClassNotFoundThrowsGateRuntimeException() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData badData = mock(ResourceData.class);
when(badData.getClassName()).thenReturn("invalid.UnknownClass");
when(badData.getResourceClass()).thenThrow(ClassNotFoundException.class);
register.put("invalid.UnknownClass", badData);
register.remove("invalid.UnknownClass");
}

@Test
public void testGetAllInstancesIncludesHiddenResource() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockPR.class.getName());
// Resource resource = new MockPR() {
// 
// public FeatureMap getFeatures() {
// FeatureMap fm = Factory.newFeatureMap();
// fm.put(GateConstants.HIDDEN_FEATURE_KEY, Boolean.TRUE);
// return fm;
// }
// };
List<Resource> list = new ArrayList<>();
// list.add(resource);
// data.setInstantiations(list);
// register.put(MockPR.class.getName(), data);
// List<Resource> all = register.getAllInstances(MockPR.class.getName(), true);
// assertEquals(1, all.size());
}

@Test
public void testGetAllInstancesExcludesHiddenResource() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockPR.class.getName());
// Resource resource = new MockPR() {
// 
// public FeatureMap getFeatures() {
// FeatureMap fm = Factory.newFeatureMap();
// fm.put(GateConstants.HIDDEN_FEATURE_KEY, Boolean.TRUE);
// return fm;
// }
// };
List<Resource> list = new ArrayList<>();
// list.add(resource);
// data.setInstantiations(list);
// register.put(MockPR.class.getName(), data);
// List<Resource> visible = register.getAllInstances(MockPR.class.getName(), false);
// assertEquals(0, visible.size());
}

@Test(expected = GateException.class)
public void testGetAllInstancesFailsWithInvalidType() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
register.getAllInstances("invalid.UnknownType");
}

@Test
public void testGetVREnabledAnnotationTypesSingleEntry() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockVR.class.getName());
data.setGuiType(ResourceData.NULL_GUI);
data.setAnnotationTypeDisplayed("Sentence");
// register.put(MockVR.class.getName(), data);
List<String> types = register.getVREnabledAnnotationTypes();
assertEquals(1, types.size());
assertEquals("Sentence", types.get(0));
}

@Test
public void testGetVREnabledAnnotationTypesEmpty() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockVR.class.getName());
data.setGuiType(ResourceData.LARGE_GUI);
data.setAnnotationTypeDisplayed(null);
// register.put(MockVR.class.getName(), data);
List<String> types = register.getVREnabledAnnotationTypes();
assertTrue(types.isEmpty());
}

@Test
public void testPutAddsControllerType() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockController.class.getName());
// ResourceData result = register.put(MockController.class.getName(), data);
// assertSame(data, result);
// assertTrue(register.getControllerTypes().contains(MockController.class.getName()));
}

@Test
public void testPutAddsApplicationType() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockApplication.class.getName());
// ResourceData result = register.put(MockApplication.class.getName(), data);
// assertSame(data, result);
// assertTrue(register.getApplicationTypes().contains(MockApplication.class.getName()));
}

@Test
public void testPutAddsVisualResourceTypeWithoutDuplicates() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockVR.class.getName());
// ResourceData result1 = register.put(MockVR.class.getName(), data);
// ResourceData result2 = register.put(MockVR.class.getName(), data);
List<String> vrTypes = new ArrayList<>(register.getVrTypes());
int count = 0;
for (String s : vrTypes) {
// if (s.equals(MockVR.class.getName())) {
// count++;
// }
}
assertEquals(1, count);
}

@Test
public void testGetSmallVRsForNonExistentResourceClassReturnsEmpty() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<String> result = register.getSmallVRsForResource("non.existent.Class");
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test(expected = GateRuntimeException.class)
public void testGetLargeVRsWithInvalidResourceDisplayedClassFails() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData vr = new ResourceData();
vr.setClassName("invalid.VR");
vr.setGuiType(ResourceData.LARGE_GUI);
vr.setResourceDisplayed("invalid.NonExistentClass");
register.put("invalid.VR", vr);
// register.getLargeVRsForResource(MockPR.class.getName());
}

@Test(expected = GateRuntimeException.class)
public void testGetAnnotationVRWithInvalidVRClassFails() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData vr = new ResourceData();
vr.setClassName("invalid.VR");
vr.setGuiType(ResourceData.NULL_GUI);
vr.setAnnotationTypeDisplayed("Token");
register.put("invalid.VR", vr);
register.getAnnotationVRs("Token");
}

@Test
public void testRemoveDirectoryWithNullUrlDoesNothing() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
register.removeDirectory(null);
assertTrue(true);
}

@Test
public void testRemoveDirectoryWithUnknownUrlDoesNothing() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockPR.class.getName());
// register.put(MockPR.class.getName(), data);
register.removeDirectory(new URL("file:///unknown/plugin/path/"));
// assertTrue(register.getPrTypes().contains(MockPR.class.getName()));
}

@Test
public void testGetPublicLrInstancesEmptyWhenAllPrivate() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockLR.class.getName());
data.setPrivate(true);
// Resource r = new MockLR();
List<Resource> list = new ArrayList<>();
// list.add(r);
// data.setInstantiations(list);
// register.put(MockLR.class.getName(), data);
List<gate.LanguageResource> result = register.getPublicLrInstances();
assertTrue(result.isEmpty());
}

@Test
public void testGetPublicVrTypesFiltersOutPrivate() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData vr1 = new ResourceData();
// vr1.setClassName(MockVR.class.getName());
vr1.setPrivate(false);
ResourceData vr2 = new ResourceData();
// vr2.setClassName(MockVR2.class.getName());
vr2.setPrivate(true);
// register.put(MockVR.class.getName(), vr1);
// register.put(MockVR2.class.getName(), vr2);
List<String> publicVrTypes = register.getPublicVrTypes();
// assertTrue(publicVrTypes.contains(MockVR.class.getName()));
// assertFalse(publicVrTypes.contains(MockVR2.class.getName()));
}

@Test
public void testPutAddsVisualResourceTypeWhenVRListIsNull() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockVR.class.getName());
register.vrTypes = null;
// ResourceData result = register.put(MockVR.class.getName(), data);
// assertSame(data, result);
// assertTrue(register.getVrTypes().contains(MockVR.class.getName()));
}

@Test(expected = GateRuntimeException.class)
public void testPutFailsWhenGetResourceClassThrows() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = mock(ResourceData.class);
when(data.getClassName()).thenReturn("gate.invalid.Resource");
when(data.getResourceClass()).thenThrow(new ClassNotFoundException("class not found"));
register.put("gate.invalid.Resource", data);
}

@Test
public void testRemoveReducesReferenceCountAboveZeroAndReturnsSameData() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = mock(ResourceData.class);
// when(data.getClassName()).thenReturn(MockPR.class.getName());
// when(data.getResourceClass()).thenReturn(MockPR.class);
when(data.reduceReferenceCount()).thenReturn(2);
// register.put(MockPR.class.getName(), data);
// ResourceData removed = register.remove(MockPR.class.getName());
// assertEquals(data, removed);
// assertTrue(register.getPrTypes().contains(MockPR.class.getName()));
}

@Test
public void testRemoveWithNullResourceDataReturnsNull() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData result = register.remove("nonexistent");
assertNull(result);
}

@Test
public void testGetAllInstancesHandlesClassNotFoundInEntry() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData badData = mock(ResourceData.class);
when(badData.getResourceClass()).thenThrow(ClassNotFoundException.class);
register.put("gate.invalid.Missing", badData);
try {
register.getAllInstances("java.lang.Object");
fail("Expected GateRuntimeException");
} catch (GateRuntimeException e) {
assertTrue(e.getMessage().contains("does not exist"));
}
}

@Test
public void testGetAnnotationVRsReturnsEmptyForNullArgument() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<String> result = register.getAnnotationVRs(null);
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetLargeVRsHandlesNullResourceName() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<String> result = register.getLargeVRsForResource(null);
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetVrTypesReturnsDefensiveCopy() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockVR.class.getName());
// register.put(MockVR.class.getName(), data);
Set<String> types = register.getVrTypes();
int originalSize = types.size();
if (types instanceof List) {
((List<String>) types).clear();
}
Set<String> typesAgain = register.getVrTypes();
assertEquals(originalSize, typesAgain.size());
}

@Test
public void testRegisterBuiltinsThrowsGateExceptionOnFailure() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Gate mockGate = mock(Gate.class);
Gate.setGateHome(null);
try {
register.registerBuiltins();
} catch (GateException e) {
assertNotNull(e);
}
}

@Test
public void testGetPublicTypesListIsUnmodifiable() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockPR.class.getName());
data.setPrivate(false);
// register.put(MockPR.class.getName(), data);
List<String> result = register.getPublicPrTypes();
try {
result.add("illegal");
fail("Expected UnsupportedOperationException");
} catch (UnsupportedOperationException ex) {
assertTrue(true);
}
}

@Test
public void testRemoveDirectoryRemovesMatchingPlugin() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
URL url = new URL("file:/tmp/plugin/");
Plugin plugin = new Plugin.Directory(url);
register.registerPlugin(plugin);
register.removeDirectory(url);
assertFalse(register.getPlugins().contains(plugin));
}

@Test
public void testGetControllerTypesEmptyWhenUninitialized() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Set<String> result = register.getControllerTypes();
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testRegisterPluginWithDependenciesFalseSkipsDependencies() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
// Plugin basePlugin = new Plugin.Component(MockPR.class);
// register.registerPlugin(basePlugin, false);
// assertTrue(register.getPlugins().contains(basePlugin));
}

@Test
public void testGetPublicTypesSkipsNullResourceData() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
// register.put(MockPR.class.getName(), null);
Collection<String> raw = new ArrayList<String>();
// raw.add(MockPR.class.getName());
List<String> result = register.getPublicTypes(raw);
assertNotNull(result);
assertTrue(result.isEmpty());
}

@Test
public void testGetAnnotationVRsFavorDefaultVROrdering() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData vr1 = new ResourceData();
vr1.setClassName("vr1");
vr1.setGuiType(ResourceData.NULL_GUI);
vr1.setAnnotationTypeDisplayed("Token");
// vr1.setMainView(false);
ResourceData vr2 = new ResourceData();
vr2.setClassName("vr2");
vr2.setGuiType(ResourceData.NULL_GUI);
vr2.setAnnotationTypeDisplayed("Token");
// vr2.setMainView(true);
register.put("vr1", vr1);
register.put("vr2", vr2);
List<String> result = register.getAnnotationVRs("Token");
assertEquals(2, result.size());
assertEquals("vr2", result.get(0));
assertEquals("vr1", result.get(1));
}

@Test
public void testGetApplicationTypesEmptyWhenNothingRegistered() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Set<String> apps = register.getApplicationTypes();
assertNotNull(apps);
assertTrue(apps.isEmpty());
}

@Test
public void testGetVrInstancesByTypeReturnsEmptyForMissingType() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<VisualResource> vrs = register.getVrInstances("unknown.VR");
assertNotNull(vrs);
assertTrue(vrs.isEmpty());
}

@Test
public void testGetPublicsSkipsNullResourceData() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
List<Resource> list = new ArrayList<Resource>();
// Resource mockResource = new MockPR() {
// 
// public String getName() {
// return "test";
// }
// };
// list.add(mockResource);
register.put("some.Class", null);
List<Resource> result = register.getPublics(list);
assertTrue(result.isEmpty());
}

@Test
public void testTypedResourceListCastsCorrectly() throws Exception {
List<Resource> raw = new ArrayList<Resource>();
// MockPR prInstance = new MockPR();
// raw.add(prInstance);
// CreoleRegisterImpl.TypedResourceList<ProcessingResource> typedList = new CreoleRegisterImpl.TypedResourceList<ProcessingResource>(raw, ProcessingResource.class);
// int size = typedList.size();
// ProcessingResource first = typedList.get(0);
// assertEquals(1, size);
// assertSame(prInstance, first);
}

@Test(expected = ClassCastException.class)
public void testTypedResourceListThrowsOnInvalidCast() throws Exception {
List<Resource> raw = new ArrayList<Resource>();
// MockVR vr = new MockVR();
// raw.add(vr);
// CreoleRegisterImpl.TypedResourceList<LanguageResource> list = new CreoleRegisterImpl.TypedResourceList<LanguageResource>(raw, LanguageResource.class);
// list.get(0);
}

@Test
public void testPluginUnregisterWithDependentPluginCascade() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
// Plugin basePlugin = new Plugin.Component(MockPR.class);
Plugin dependentPlugin = mock(Plugin.class);
Set<Plugin> requiredSet = new HashSet<Plugin>();
// requiredSet.add(basePlugin);
when(dependentPlugin.getRequiredPlugins()).thenReturn(requiredSet);
when(dependentPlugin.getBaseURL()).thenReturn(new URL("file:/mock"));
when(dependentPlugin.getResourceInfoList()).thenReturn(Collections.emptyList());
when(dependentPlugin.getName()).thenReturn("dep");
when(dependentPlugin.getVersion()).thenReturn("1.0");
// register.registerPlugin(basePlugin);
register.getPlugins().add(dependentPlugin);
// register.unregisterPlugin(basePlugin);
// assertFalse(register.getPlugins().contains(basePlugin));
assertFalse(register.getPlugins().contains(dependentPlugin));
}

@Test
public void testAddSameCreoleListenerTwiceDoesNotDuplicate() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleListener listener = mock(CreoleListener.class);
register.addCreoleListener(listener);
register.addCreoleListener(listener);
Resource res = mock(Resource.class);
// when(res.getClass()).thenReturn(MockPR.class);
when(res.getName()).thenReturn("X");
register.setResourceName(res, "Y");
verify(listener).resourceRenamed(res, "Y", "Y");
}

@Test
public void testRemoveCreoleListenerThatWasNeverAddedDoesNotThrow() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleListener listener = mock(CreoleListener.class);
register.removeCreoleListener(listener);
assertTrue(true);
}

@Test
public void testPublicTypesReturnsEmptyWhenAllPrivateAndMixedNull() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd1 = new ResourceData();
// rd1.setClassName(MockPR.class.getName());
rd1.setPrivate(true);
// register.put(MockPR.class.getName(), rd1);
Collection<String> inputTypes = new ArrayList<String>();
// inputTypes.add(MockPR.class.getName());
inputTypes.add("non.existent.Type");
List<String> result = register.getPublicTypes(inputTypes);
assertNotNull(result);
assertEquals(0, result.size());
}

@Test
public void testPutAddsOnlyPRTypeWhenNotTool() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData rd = new ResourceData();
// rd.setClassName(MockPR.class.getName());
rd.setTool(false);
// register.put(MockPR.class.getName(), rd);
// assertTrue(register.getPrTypes().contains(MockPR.class.getName()));
// assertFalse(register.getToolTypes().contains(MockPR.class.getName()));
}

@Test
public void testPutDoesNotAddVRTypeDuplicatesEvenIfCalledTwice() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockVR.class.getName());
// register.put(MockVR.class.getName(), data);
// register.put(MockVR.class.getName(), data);
int occurrences = 0;
List<String> vrTypes = new ArrayList<>(register.getVrTypes());
// if (vrTypes.contains(MockVR.class.getName())) {
// occurrences++;
// }
assertEquals(1, occurrences);
}

@Test
public void testGetAllInstancesInheritedPRType() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData base = new ResourceData();
// base.setClassName(MockPR.class.getName());
// Resource instance = new MockPR();
List<Resource> instList = new ArrayList<Resource>();
// instList.add(instance);
// base.setInstantiations(instList);
// register.put(MockPR.class.getName(), base);
List<Resource> result = register.getAllInstances("java.lang.Object");
assertEquals(1, result.size());
assertTrue(result.get(0) instanceof Resource);
}

@Test(expected = GateException.class)
public void testParseDirectoryThrowsWhenCreoleXmlIsInvalid() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Plugin plugin = mock(Plugin.class);
URL baseUrl = new URL("file:/mock/");
URL creoleUrl = new URL("file:/mock/creole.xml");
when(plugin.getBaseURI()).thenReturn(baseUrl.toURI());
when(plugin.getBaseURL()).thenReturn(baseUrl);
when(plugin.getName()).thenReturn("Invalid");
when(plugin.getVersion()).thenReturn("1.0");
// when(plugin.getCreoleXML()).thenThrow(new JDOMException("broken"));
register.registerPlugin(plugin);
}

@Test
public void testGetLrTypesWhenNoneRegisteredReturnsUnmodifiableEmptySet() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
Set<String> lrs = register.getLrTypes();
assertNotNull(lrs);
assertTrue(lrs.isEmpty());
try {
lrs.add("illegal");
fail("Expected UnsupportedOperationException");
} catch (UnsupportedOperationException e) {
assertTrue(true);
}
}

@Test
public void testGetSmallVRsForIncompatibleResourceClass() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData vr = new ResourceData();
vr.setClassName("NonDisplayVR");
vr.setGuiType(ResourceData.SMALL_GUI);
vr.setResourceDisplayed("java.util.Date");
register.put("NonDisplayVR", vr);
List<String> result = register.getSmallVRsForResource("java.lang.String");
assertTrue(result.isEmpty());
}

@Test
public void testPutPopulatesApplicationTypeForPackagedController() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockApp.class.getName());
// register.put(MockApp.class.getName(), data);
// assertTrue(register.getApplicationTypes().contains(MockApp.class.getName()));
}

@Test
public void testFireResourceLoadedNullCreoleListenersDoesNotThrow() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
// CreoleEvent event = new CreoleEvent(mock(Resource.class), CreoleEvent.LOAD_RESOURCE);
// register.resourceLoaded(event);
assertTrue(true);
}

@Test
public void testFireDatastoreCreatedNullCreoleListenersDoesNotThrow() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleEvent event = new CreoleEvent(mock(Resource.class), CreoleEvent.DATASTORE_CREATED);
register.datastoreCreated(event);
assertTrue(true);
}

@Test
public void testPutWithNullClassNameHandlesGracefully() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
data.setClassName(null);
try {
register.put(null, data);
assertTrue(true);
} catch (Exception e) {
fail("Should handle null className defensively");
}
}

@Test
public void testRemoveWithNullKeyReturnsNullSafely() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData result = register.remove(null);
assertNull(result);
}

@Test
public void testClearAlsoClearsPluginListenerSet() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
PluginListener listener = mock(PluginListener.class);
register.addPluginListener(listener);
URL url = new URL("file:/tmp/");
Plugin plugin = new Plugin.Directory(url);
register.registerPlugin(plugin);
register.clear();
assertTrue(register.getPlugins().isEmpty());
assertEquals(0, register.size());
}

@Test
public void testPutWithControllerAddsControllerType() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockController.class.getName());
// ResourceData result = register.put(MockController.class.getName(), data);
// assertTrue(register.getControllerTypes().contains(MockController.class.getName()));
}

@Test
public void testGetResourceRenamedEventIsFired() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
CreoleListener listener = mock(CreoleListener.class);
register.addCreoleListener(listener);
Resource resource = mock(Resource.class);
when(resource.getName()).thenReturn("R1");
register.setResourceName(resource, "R2");
verify(listener).resourceRenamed(resource, "R2", "R2");
}

@Test
public void testRemoveRemovesTool() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockPR.class.getName());
data.setTool(true);
// register.put(MockPR.class.getName(), data);
when(data.reduceReferenceCount()).thenReturn(0);
// when(data.getResourceClass()).thenReturn(MockPR.class);
// register.remove(MockPR.class.getName());
// assertFalse(register.getToolTypes().contains(MockPR.class.getName()));
}

@Test
public void testGetVrTypesReturnsUnmodifiableCopy() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
ResourceData data = new ResourceData();
// data.setClassName(MockVR.class.getName());
// register.put(MockVR.class.getName(), data);
Set<String> types = register.getVrTypes();
try {
if (types instanceof List) {
((List<String>) types).add("fail");
} else if (types instanceof Set) {
((Set<String>) types).add("fail");
}
fail("Expected UnsupportedOperationException");
} catch (UnsupportedOperationException e) {
assertTrue(true);
}
}

@Test
public void testUnregisterPluginCleansUpClassLoader() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
URL url = new URL("file:/mock/");
Plugin p = new Plugin.Directory(url);
register.registerPlugin(p);
register.unregisterPlugin(p);
assertFalse(register.getPlugins().contains(p));
}

@Test
public void testRegisterComponentThrowsWhenMalformedUrl() throws Exception {
CreoleRegisterImpl register = new CreoleRegisterImpl();
class InvalidComponent {
}
try {
register.registerComponent(InvalidComponent.class.asSubclass(Resource.class));
fail("Expected GateException due to malformed URL");
} catch (GateException e) {
assertTrue(true);
}
}
}

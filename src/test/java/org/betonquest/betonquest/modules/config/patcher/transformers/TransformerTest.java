package org.betonquest.betonquest.modules.config.patcher.transformers;

import org.betonquest.betonquest.api.config.patcher.PatchTransformerRegisterer;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.modules.config.Patcher;
import org.betonquest.betonquest.modules.config.patcher.DefaultPatchTransformerRegisterer;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This test tests all config transformers.
 */
@ExtendWith(BetonQuestLoggerService.class)
class TransformerTest {

    /**
     * Anonymous {@link PatchTransformerRegisterer} for testing.
     */
    public static final PatchTransformerRegisterer REGISTERER = new DefaultPatchTransformerRegisterer();

    /**
     * The file that contains a demo config for this test.
     */
    private static final File CONFIG_FILE = new File("src/test/resources/modules.config/config.yml");

    /**
     * The demo config that is used for this test.
     */
    private static final YamlConfiguration CONFIG = new YamlConfiguration();

    @BeforeEach
    void resetConfig() throws IOException, InvalidConfigurationException {
        CONFIG.load(CONFIG_FILE);
    }

    @Test
    void testValueRename(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                  - type: VALUE_RENAME
                    key: section.testKey
                    oldValueRegex: test
                    newValue: newTest
                """;
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);

        CONFIG.set("section.testKey", "newTest");
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testValueRenameErrorMissingKey(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                  - type: VALUE_RENAME
                    key: section.invalidKey
                    oldValueRegex: test
                    newValue: newTest
                """;
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);
        validateLogging(logger, "VALUE_RENAME", "The key 'section.invalidKey' did not exist, skipping transformation.");
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testValueRenameErrorNoMatchForValue(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                  - type: VALUE_RENAME
                    key: section.testKey
                    oldValueRegex: noMatchRegex
                    newValue: newTest
                """;
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);
        validateLogging(logger, "VALUE_RENAME", "Value does not match the given regex, skipping transformation.");
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testListEntryAddLast(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                    - type: LIST_ENTRY_ADD
                      key: section.myList
                      entry: newEntry
                      position: LAST
                """;
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);

        final List<String> list = CONFIG.getStringList("section.myList");
        list.add("newEntry");
        CONFIG.set("section.myList", list);
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testListEntryAddFirst(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                    - type: LIST_ENTRY_ADD
                      key: section.myList
                      entry: newEntry
                      position: FIRST
                """;
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);

        final List<String> list = CONFIG.getStringList("section.myList");
        final List<String> newList = new ArrayList<>();
        newList.add("newEntry");
        newList.addAll(list);
        CONFIG.set("section.myList", newList);
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testListEntryAddDefault(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                    - type: LIST_ENTRY_ADD
                      key: section.myList
                      entry: newEntry
                """;
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);

        final List<String> list = CONFIG.getStringList("section.myList");
        list.add("newEntry");
        CONFIG.set("section.myList", list);
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testListEntryAddRubbish(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                    - type: LIST_ENTRY_ADD
                      key: section.myList
                      entry: newEntry
                      position: rubbish
                """;
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);

        final List<String> list = CONFIG.getStringList("section.myList");
        list.add("newEntry");
        CONFIG.set("section.myList", list);
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testListEntryAddMissingKey(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                    - type: LIST_ENTRY_ADD
                      key: section.invalidKey
                      entry: newEntry
                      position: LAST
                """;
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);

        final List<String> list = CONFIG.getStringList("section.invalidKey");
        list.add("newEntry");
        CONFIG.set("section.invalidKey", list);

        validateLogging(logger, "LIST_ENTRY_ADD", "List 'section.invalidKey' did not exist, so it was created.");
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testListEntryRename(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                  - type: LIST_ENTRY_RENAME
                    key: section.myList
                    oldEntryRegex: currentEntry
                    newEntry: newEntry
                """;
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);

        final List<String> list = CONFIG.getStringList("section.myList");
        final int index = list.indexOf("currentEntry");
        list.set(index, "newEntry");
        CONFIG.set("section.myList", list);

        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testListEntryRenameMissingKey(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                  - type: LIST_ENTRY_RENAME
                    key: section.invalidKey
                    oldEntryRegex: currentEntry
                    newEntry: newEntry
                """;
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);
        CONFIG.set("section.invalidKey", new ArrayList<String>());

        validateLogging(logger, "LIST_ENTRY_RENAME", "List 'section.invalidKey' did not exist, so an empty list was created.");
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testListEntryRenameNoMatchRegex(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                  - type: LIST_ENTRY_RENAME
                    key: section.myList
                    oldEntryRegex: invalidRegex
                    newEntry: newEntry
                """;
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);
        validateLogging(logger, "LIST_ENTRY_RENAME", "Tried to rename 'invalidRegex' with 'newEntry' but there was no such element in the list 'section.myList'.");
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testListRemove(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                  2.0.0.1:
                    - type: LIST_ENTRY_REMOVE
                      key: section.myList
                      entry: removedEntry
                """;
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);

        final List<?> list = CONFIG.getList("section.myList");
        assertNotNull(list, "List was null.");
        list.remove("removedEntry");
        CONFIG.set("section.myList", list);

        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testListRemoveNonExisting(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                  2.0.0.1:
                    - type: LIST_ENTRY_REMOVE
                      key: section.invalidList
                      entry: removedEntry
                """;
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);
        CONFIG.set("section.invalidList", new ArrayList<String>());
        validateLogging(logger, "LIST_ENTRY_REMOVE", "List 'section.invalidList' did not exist, so an empty list was created.");
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testListRemoveInvalidKey(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                  2.0.0.1:
                    - type: LIST_ENTRY_REMOVE
                      key: section.myList
                      entry: invalidEntry
                """;
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);

        validateLogging(logger, "LIST_ENTRY_REMOVE", "Tried to remove 'invalidEntry' but there was no such element in the list 'section.myList'.");
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testRemove(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                    - type: REMOVE
                      key: section.myList
                """;
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);

        CONFIG.set("section.myList", null);

        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testRemoveNonExistent(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                    - type: REMOVE
                      key: section.nonExistent
                """;
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);

        validateLogging(logger, "REMOVE", "Key 'section.nonExistent' did not exist, so it was not deleted.");

        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testKeyRename(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                    - type: KEY_RENAME
                      oldKey: section.test
                      newKey: section.testNew
                """;
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);

        final String value = CONFIG.getString("section.test");
        CONFIG.set("section.test", null);
        CONFIG.set("section.testNew", value);

        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testKeyRenameList(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                    - type: KEY_RENAME
                      oldKey: section.myList
                      newKey: section.newList
                """;
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);

        final Object value = CONFIG.get("section.myList");
        CONFIG.set("section.myList", null);
        CONFIG.set("section.newList", value);

        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testKeyRenameNonExisting(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                2.0.0.1:
                    - type: KEY_RENAME
                      oldKey: section.invalid
                      newKey: section.testNew
                """;
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);

        validateLogging(logger, "KEY_RENAME", "Key 'section.invalid' was not set, skipping transformation to 'section.testNew'.");
        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testSet(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                  2.0.0.1:
                    - type: SET
                      key: journalLock
                      value: true
                """;
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);

        CONFIG.set("journalLock", "true");

        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testTransformStringToBoolean(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                  2.0.0.1:
                    - type: TYPE_TRANSFORM
                      key: section.boolean
                      newType: boolean
                """;

        CONFIG.set("section.boolean", true);
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);

        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testTransformStringToInt(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                  2.0.0.1:
                    - type: TYPE_TRANSFORM
                      key: section.int
                      newType: int
                """;

        CONFIG.set("section.int", 3);
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);

        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testTransformStringToFloat(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                  2.0.0.1:
                    - type: TYPE_TRANSFORM
                      key: section.float
                      newType: float
                """;

        CONFIG.set("section.float", 2.5F);
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);

        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testTransformStringToDouble(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                  2.0.0.1:
                    - type: TYPE_TRANSFORM
                      key: section.double
                      newType: double
                """;

        CONFIG.set("section.double", 2.123_456_789_123_456_7D);
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);

        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    @Test
    void testTransformBooleanToString(final BetonQuestLogger logger) throws InvalidConfigurationException {
        final String patch = """
                  2.0.0.1:
                    - type: TYPE_TRANSFORM
                      key: section.boolean
                      newType: string
                """;

        CONFIG.set("section.double", "true");
        final String serializedConfig = getSerializedPatchedConfig(logger, patch);

        assertEquals(CONFIG.saveToString(), serializedConfig, "Patch was not applied correctly.");
    }

    private String getSerializedPatchedConfig(final BetonQuestLogger logger, final String patch) throws InvalidConfigurationException {
        final YamlConfiguration patchConfig = new YamlConfiguration();
        patchConfig.loadFromString(patch);

        //Config must be "cloned", otherwise the tests would compare the same object
        final YamlConfiguration questConfig = new YamlConfiguration();
        questConfig.loadFromString(CONFIG.saveToString());

        //New version is automatically set for all tests
        CONFIG.set("configVersion", "2.0.0-CONFIG-1");

        final Patcher patcher = new Patcher(logger, questConfig, patchConfig);
        REGISTERER.registerTransformers(patcher);
        patcher.patch();
        return questConfig.saveToString();
    }

    private void validateLogging(final BetonQuestLogger logger, final String transformerType, final String exceptionMessage) {
        verify(logger, times(1)).info("Applying patches to update to '2.0.0-CONFIG-1'...");
        verify(logger, times(1)).info("Applying patch of type '" + transformerType + "'...");
        verify(logger, times(1)).warn("There has been an issue while applying the patches for '2.0.0.1': " + exceptionMessage);
        verifyNoMoreInteractions(logger);
    }
}

package net.sodiumzh.nautils.savedata.redirector;

import net.minecraft.resources.ResourceLocation;

/**
 * NaUtils - SaveDataLocationRedirector is a utility module to simplify redirect object registry location in save data 
 * when a mod is renaming the objects' registry keys. 
 * <p>Now it's incomplete and only for NFF rename.
 * <p>Use: {@code SaveDataLocationRedirector.get()} to get the instance which allows chaining.
 */
public class SaveDataLocationRedirector
{
	private static final SaveDataLocationRedirector INSTANCE = new SaveDataLocationRedirector();
	
	private SaveDataLocationRedirector() {}
	
	public static SaveDataLocationRedirector get() {
		return INSTANCE;
	}
	
	/*public SaveDataLocationRedirector portBlock(ResourceLocation oldKey, ResourceLocation newKey)
	{
		SaveDataLocationRedirectorRegistries.BLOCK_MAPPING.put(oldKey, newKey);
		return this;
	}*/
	public SaveDataLocationRedirector redirectItem(ResourceLocation oldKey, ResourceLocation newKey)
	{
		SaveDataLocationRedirectorRegistries.ITEM_MAPPING.put(oldKey, newKey);
		return this;
	}

	public SaveDataLocationRedirector redirectEntityType(ResourceLocation oldKey, ResourceLocation newKey) 
	{
		SaveDataLocationRedirectorRegistries.ENTITY_TYPE_MAPPING.put(oldKey, newKey);
		return this;
	}

	public SaveDataLocationRedirector redirectEntityCapability(ResourceLocation oldKey, ResourceLocation newKey) 
	{
		SaveDataLocationRedirectorRegistries.ENTITY_CAPABILITY_MAPPING.put(oldKey, newKey);
		return this;
	}

	public SaveDataLocationRedirector redirectLevelCapability(ResourceLocation oldKey, ResourceLocation newKey)
	{
		SaveDataLocationRedirectorRegistries.LEVEL_CAPABILITY_MAPPING.put(oldKey, newKey);
		return this;
	}
	
	/**
	 * Port all items, entity types, entity and level capabilities
	 * from a namespace to another. Note this operation is <i>after</i> single-key redirection e.g. {@code portItem}.
	 * <p>For example, single-key redirection maps {@code "oldmod:some_item"} to {@code "oldmod:some_other_item"}, and namespace porting
	 * maps {@code "oldmod"} to {@code "newmod"}, then finally it will be {@code "newmod:some_other_item"}.
	 * <p>For another example, single-key redirection maps {@code "oldmod:some_item"} to {@code "newmod:some_other_item"}, and namespace redirection
	 * maps {@code "oldmod"} to {@code "othermod"}, then finally it will be {@code "newmod:some_other_item"}. The new namespace 
	 * will be {@code "newmod"} instead of {@code "othermod"} because single-key redirection has mapped the namespace to {@code "newmod"} and 
	 * thus the new namespace isn't affected by namespace renaming of {@code "oldmod"}.
	 */
	public SaveDataLocationRedirector redirectNamespace(String oldNamespace, String newNamespace)
	{
		SaveDataLocationRedirectorRegistries.NAMESPACE_MAPPING.put(oldNamespace, newNamespace);
		return this;
	}
	
}

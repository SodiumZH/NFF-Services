package net.sodiumzh.nfu.savedata.redirector;

import net.minecraft.resources.ResourceLocation;
import net.sodiumzh.nfu.object.LimitedMutable;

/**
 * NFU - SaveDataLocationRedirector is a utility module to simplify redirect object registry location in save data 
 * when a mod is renaming the objects' registry keys. 
 * <p>Now it's incomplete and only for NFF rename.
 * <p>Use: {@code SaveDataLocationRedirector.get()} to get the instance which allows chaining.
 */
public class SaveDataLocationRedirector
{
	private static final SaveDataLocationRedirector INSTANCE = new SaveDataLocationRedirector();
	private static LimitedMutable<Boolean> LOADING_COMPLETED = new LimitedMutable<>(false, 1);

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
	 * from a namespace to another. Note this operation is <i>before</i> single-key redirection e.g. {@code portItem} because
	 * the namespace redirection is done in {@link ResourceLocation} creating i.e. the old {@link ResourceLocation}s will
	 * be totally impossible to create.
	 * <p>For example, single-key redirection maps {@code "oldmod:some_item"} to {@code "oldmod:some_other_item"}, and namespace porting
	 * maps {@code "oldmod"} to {@code "newmod"}, then finally it will be {@code "newmod:some_item"}. This is because
	 * namespace redirection has ported {@code "oldmod:some_item" to "newmod:some_item"} and thus doesn't hit the single-key
	 * redirection.
	 * <p>For another example, single-key redirection maps {@code "newmod:some_item"} to {@code "newmod:some_other_item"}, and namespace redirection
	 * maps {@code "oldmod"} to {@code "newmod"}, then finally {@code "oldmod:some_item"} will be {@code "newmod:some_other_item"}. The namespace
	 * porting first ports {@code "oldmod:some_item"} to {@code "newmod:some_item"}, then single-key ports {@code "newmod:some_item"}
	 * to {@code "newmod:some_other_item"}.
	 */
	public SaveDataLocationRedirector redirectNamespace(String oldNamespace, String newNamespace)
	{
		SaveDataLocationRedirectorRegistries.NAMESPACE_MAPPING.put(oldNamespace, newNamespace);
		return this;
	}

	static void setLoadingCompleted() {
		LOADING_COMPLETED.trySet(true);
	}

	public static boolean isLoadingCompleted() {
		return LOADING_COMPLETED.get();
	}

}

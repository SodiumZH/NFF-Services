package net.sodiumzh.nautils.statics;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.sodiumzh.nautils.NaUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.function.Consumer;

public class NaUtilsDataStatics {

    /**
     * Read all jsons at a given location on server side. On client or if server hasn't started, do nothing.
     * @param location Location of the json.
     * @param reader Actions to do for each json. Note that this reader may be invoked multiple times if there are
     *               multiple data packs present. If IO or runtime exception occurs during invoking, it will not throw the
     *               exception out but print stack trace and continue reading next json.
     * @param suppressStackTrace If true, when an exception is caught, it will not print stack trace and just continue on next json.
     */
    public static void readJsonsServerSide(ResourceLocation location, Consumer<JsonElement> reader, boolean suppressStackTrace)
    {
        MinecraftServer server = NaUtils.getServer();
        if (server == null) return;
        ResourceManager mgr = server.getResourceManager();

        List<Resource> resources;
        try {
            resources = mgr.getResources(location);
        } catch (IOException e) {
            throw new RuntimeException("NaUtilsDataStatics#readJsonsServerSide: IOException thrown on getting resource stack.", e);
        }
        for (Resource r: resources)
        {
            try {
                InputStream input = r.getInputStream();
                Reader inputReader = new InputStreamReader(input);
                JsonElement json = JsonParser.parseReader(inputReader);
                reader.accept(json);
            } catch (RuntimeException e) {
                if (!suppressStackTrace)
                    e.printStackTrace();
            }
        }
    }

    /**
     * Read all jsons at a given location on server side. On client or if server hasn't started, do nothing.
     * @param location Location of the json.
     * @param reader Actions to do for each json. Note that this reader may be invoked multiple times if there are
     *               multiple data packs present. If IO or runtime exception occurs during invoking, it will not throw the
     *               exception out but print stack trace and continue reading next json.
     */
    public static void readJsonsServerSide(ResourceLocation location, Consumer<JsonElement> reader)
    {
        readJsonsServerSide(location, reader, false);
    }
}

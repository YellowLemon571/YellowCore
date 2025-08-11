package net.yelw.yellowcore;

import org.slf4j.Logger;

import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforgespi.language.IModInfo;
import net.yelw.yellowcore.item.ResizerGrow;
import net.yelw.yellowcore.item.ResizerShrink;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(YellowCore.MODID)
public class YellowCore {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "yellowcore";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "yellowcore" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "yellowcore" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "yellowcore" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Initialize pokemon scaling values to defaults
    public static float minScale = 0.2f;
    public static float maxScale = 2.0f;
    public static float scaleChange = 0.2f;

    // Register the pokemon resizing items
    public static final DeferredItem<Item> RESIZER_GROW = ITEMS.register(
        "growth_salve",
        () -> new ResizerGrow(new Item.Properties().stacksTo(16))
    
    );
    public static final DeferredItem<Item> RESIZER_SHRINK = ITEMS.register(
        "shrink_salve",
        () -> new ResizerShrink(new Item.Properties().stacksTo(16))
    );

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public YellowCore(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (YellowCore) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Get the mod's info and log it
        IModInfo modInfo = ModList.get().getModContainerById(YellowCore.MODID).orElse(null).getModInfo();
        String version = modInfo.getVersion().toString();
        LOGGER.info("YellowCore v" + version + " by Yellow571");

        // Get config values for pokemon scaling
        minScale = Config.SCALE_MIN.get().floatValue();
        maxScale = Config.SCALE_MAX.get().floatValue();
        scaleChange = Config.SCALE_CHANGE.get().floatValue();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
    }

    // Method for in/decrementing a pokemon's size
    // Returns true on success, otherwise false
    public static boolean resizePokemon(Player player, PokemonEntity pokemonEntity, float amount) {
        Pokemon pokemon = pokemonEntity.getPokemon();
        Level level = player.level();

        // Check if the pokemon's current scale modifier is at the max or min
        float oldScale = Math.round(pokemon.getScaleModifier() * 100f) / 100f;
        if (((amount > 0) && (oldScale == maxScale)) || ((amount < 0) && (oldScale == minScale))) return false;

        // Set the new scale modifier
        float newScale = oldScale + amount;
        if ((amount > 0) && (newScale > maxScale)) newScale = maxScale;
        else if ((amount < 0) && (newScale < minScale)) newScale = minScale;
        pokemon.setScaleModifier(newScale);
                
        // Respawn the pokemon to show the new scale
        Vec3 position = pokemonEntity.position();
        pokemonEntity.discard();
        PokemonEntity newPokemonEntity = new PokemonEntity(level, pokemon, CobblemonEntities.POKEMON);
        newPokemonEntity.moveTo(position);
        level.addFreshEntity(newPokemonEntity);
        newPokemonEntity.playSound(CobblemonSounds.BERRY_EAT);
        LOGGER.debug(player.getName().getString() + " changed the scale multiplier of their " + pokemon.getDisplayName().getString() + " to " + Float.toString(newScale));

        return true;
    }
}

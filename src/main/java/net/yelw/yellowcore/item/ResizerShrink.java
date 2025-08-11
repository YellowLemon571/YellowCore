package net.yelw.yellowcore.item;

import java.util.List;

import javax.annotation.Nonnull;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.yelw.yellowcore.YellowCore;

public class ResizerShrink extends Item {
    
    // Initialize the item with default properties
    public ResizerShrink(Properties properties) {
        super(properties);
    }

    // Add tooltip text
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.yellowcore.shrink_salve").withColor(0xAAAAAA));
    }

    // Event handler for using the item on an entity
    @Override
    public InteractionResult interactLivingEntity(@Nonnull ItemStack stack, @Nonnull Player player, @Nonnull LivingEntity interactionTarget, @Nonnull InteractionHand usedHand) {
        // Handle the event on serverside only
        if (!player.level().isClientSide) {
            // Check if the entity is a pokemon
            if (interactionTarget instanceof PokemonEntity) {
                PokemonEntity pokemonEntity = (PokemonEntity) interactionTarget;
                Pokemon pokemon = pokemonEntity.getPokemon();

                // Check if the player owns the pokemon
                if (!pokemon.isPlayerOwned() || !pokemon.belongsTo(player)) {
                    player.displayClientMessage(Component.literal("You do not own this pokemon!"), true);
                    return InteractionResult.FAIL;
                }

                if (!YellowCore.resizePokemon(player, pokemonEntity, -YellowCore.scaleChange)) {
                    player.displayClientMessage(Component.literal("It won't have any effect."), true);
                    return InteractionResult.FAIL;
                }

                // Consume the item
                if (!player.isCreative()) stack.shrink(1);
            }
        }
        
        return InteractionResult.sidedSuccess(player.level().isClientSide);
    }

}

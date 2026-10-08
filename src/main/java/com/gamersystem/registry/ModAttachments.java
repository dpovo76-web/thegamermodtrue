package com.gamersystem.registry;

import com.gamersystem.GamerSystem;
import com.gamersystem.data.GamerData;
import java.util.function.Supplier;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, GamerSystem.MODID);

    public static final Supplier<AttachmentType<GamerData>> GAMER = ATTACHMENTS.register("gamer",
            () -> AttachmentType.builder(() -> GamerData.DEFAULT)
                    .serialize(GamerData.CODEC)
                    .copyOnDeath()
                    .build());
}

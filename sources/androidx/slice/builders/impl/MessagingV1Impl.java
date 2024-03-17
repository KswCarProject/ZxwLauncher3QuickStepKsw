package androidx.slice.builders.impl;

import android.graphics.drawable.Icon;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceSpec;
import androidx.slice.builders.impl.MessagingBuilder;

public class MessagingV1Impl extends TemplateBuilderImpl implements MessagingBuilder {
    public void apply(Slice.Builder builder) {
    }

    public MessagingV1Impl(Slice.Builder builder, SliceSpec sliceSpec) {
        super(builder, sliceSpec);
    }

    public void add(TemplateBuilderImpl templateBuilderImpl) {
        getBuilder().addSubSlice(templateBuilderImpl.build(), "message");
    }

    public TemplateBuilderImpl createMessageBuilder() {
        return new MessageBuilder(this);
    }

    public static final class MessageBuilder extends TemplateBuilderImpl implements MessagingBuilder.MessageBuilder {
        public void apply(Slice.Builder builder) {
        }

        public MessageBuilder(MessagingV1Impl messagingV1Impl) {
            super(messagingV1Impl.createChildBuilder(), (SliceSpec) null);
        }

        public void addSource(Icon icon) {
            getBuilder().addIcon(IconCompat.createFromIcon(icon), "source", new String[0]);
        }

        public void addText(CharSequence charSequence) {
            getBuilder().addText(charSequence, (String) null, new String[0]);
        }

        public void addTimestamp(long j) {
            getBuilder().addTimestamp(j, (String) null, new String[0]);
        }
    }
}

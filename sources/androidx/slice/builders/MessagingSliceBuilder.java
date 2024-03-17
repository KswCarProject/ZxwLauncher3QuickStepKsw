package androidx.slice.builders;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.util.Consumer;
import androidx.slice.SliceSpecs;
import androidx.slice.builders.impl.MessagingBasicImpl;
import androidx.slice.builders.impl.MessagingBuilder;
import androidx.slice.builders.impl.MessagingListV1Impl;
import androidx.slice.builders.impl.MessagingV1Impl;
import androidx.slice.builders.impl.TemplateBuilderImpl;

public class MessagingSliceBuilder extends TemplateSliceBuilder {
    public static final int MAXIMUM_RETAINED_MESSAGES = 50;
    MessagingBuilder mBuilder;

    public MessagingSliceBuilder(Context context, Uri uri) {
        super(context, uri);
    }

    public MessagingSliceBuilder add(MessageBuilder messageBuilder) {
        this.mBuilder.add((TemplateBuilderImpl) messageBuilder.mImpl);
        return this;
    }

    public MessagingSliceBuilder add(Consumer<MessageBuilder> consumer) {
        MessageBuilder messageBuilder = new MessageBuilder(this);
        consumer.accept(messageBuilder);
        return add(messageBuilder);
    }

    /* access modifiers changed from: package-private */
    public void setImpl(TemplateBuilderImpl templateBuilderImpl) {
        this.mBuilder = (MessagingBuilder) templateBuilderImpl;
    }

    /* access modifiers changed from: protected */
    public TemplateBuilderImpl selectImpl(Uri uri) {
        if (checkCompatible(SliceSpecs.MESSAGING, uri)) {
            return new MessagingV1Impl(getBuilder(), SliceSpecs.MESSAGING);
        }
        if (checkCompatible(SliceSpecs.LIST, uri)) {
            return new MessagingListV1Impl(getBuilder(), SliceSpecs.LIST);
        }
        if (checkCompatible(SliceSpecs.BASIC, uri)) {
            return new MessagingBasicImpl(getBuilder(), SliceSpecs.BASIC);
        }
        return null;
    }

    public static final class MessageBuilder extends TemplateSliceBuilder {
        MessagingBuilder.MessageBuilder mImpl;

        public MessageBuilder(MessagingSliceBuilder messagingSliceBuilder) {
            super(messagingSliceBuilder.mBuilder.createMessageBuilder());
        }

        public MessageBuilder addSource(Icon icon) {
            this.mImpl.addSource(icon);
            return this;
        }

        public MessageBuilder addSource(IconCompat iconCompat) {
            if (Build.VERSION.SDK_INT >= 23) {
                this.mImpl.addSource(iconCompat.toIcon());
            }
            return this;
        }

        public MessageBuilder addText(CharSequence charSequence) {
            this.mImpl.addText(charSequence);
            return this;
        }

        public MessageBuilder addTimestamp(long j) {
            this.mImpl.addTimestamp(j);
            return this;
        }

        /* access modifiers changed from: package-private */
        public void setImpl(TemplateBuilderImpl templateBuilderImpl) {
            this.mImpl = (MessagingBuilder.MessageBuilder) templateBuilderImpl;
        }
    }
}

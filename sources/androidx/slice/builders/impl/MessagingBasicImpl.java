package androidx.slice.builders.impl;

import android.graphics.drawable.Icon;
import android.os.Build;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceSpec;
import androidx.slice.builders.impl.MessagingBuilder;
import com.android.launcher3.LauncherSettings;

public class MessagingBasicImpl extends TemplateBuilderImpl implements MessagingBuilder {
    private MessageBuilder mLastMessage;

    public MessagingBasicImpl(Slice.Builder builder, SliceSpec sliceSpec) {
        super(builder, sliceSpec);
    }

    public void apply(Slice.Builder builder) {
        if (this.mLastMessage != null) {
            Slice.Builder addHints = new Slice.Builder(getBuilder()).addHints("list_item");
            if (Build.VERSION.SDK_INT >= 23 && this.mLastMessage.mIcon != null) {
                addHints.addSubSlice(this.mLastMessage.mIcon);
            }
            if (this.mLastMessage.mText != null) {
                addHints.addText(this.mLastMessage.mText, (String) null, new String[0]);
            }
            builder.addSubSlice(addHints.build());
        }
    }

    public void add(TemplateBuilderImpl templateBuilderImpl) {
        MessageBuilder messageBuilder = (MessageBuilder) templateBuilderImpl;
        MessageBuilder messageBuilder2 = this.mLastMessage;
        if (messageBuilder2 == null || messageBuilder2.mTimestamp < messageBuilder.mTimestamp) {
            this.mLastMessage = messageBuilder;
        }
    }

    public TemplateBuilderImpl createMessageBuilder() {
        return new MessageBuilder(this);
    }

    public static final class MessageBuilder extends TemplateBuilderImpl implements MessagingBuilder.MessageBuilder {
        Slice mIcon;
        CharSequence mText;
        long mTimestamp;

        public void apply(Slice.Builder builder) {
        }

        public MessageBuilder(MessagingBasicImpl messagingBasicImpl) {
            this(messagingBasicImpl.createChildBuilder());
        }

        private MessageBuilder(Slice.Builder builder) {
            super(builder, (SliceSpec) null);
        }

        public void addSource(Icon icon) {
            this.mIcon = getBuilder().addIcon(IconCompat.createFromIcon(icon), "source", "no_tint").addHints(LauncherSettings.Favorites.TITLE).build();
        }

        public void addText(CharSequence charSequence) {
            this.mText = charSequence;
        }

        public void addTimestamp(long j) {
            this.mTimestamp = j;
        }
    }
}

package androidx.slice.builders.impl;

import android.graphics.drawable.Icon;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceSpec;
import androidx.slice.builders.impl.ListBuilderV1Impl;
import androidx.slice.builders.impl.MessagingBuilder;

public class MessagingListV1Impl extends TemplateBuilderImpl implements MessagingBuilder {
    private final ListBuilderV1Impl mListBuilder;

    public MessagingListV1Impl(Slice.Builder builder, SliceSpec sliceSpec) {
        super(builder, sliceSpec);
        ListBuilderV1Impl listBuilderV1Impl = new ListBuilderV1Impl(builder, sliceSpec);
        this.mListBuilder = listBuilderV1Impl;
        listBuilderV1Impl.setTtl(-1);
    }

    public void add(TemplateBuilderImpl templateBuilderImpl) {
        this.mListBuilder.addRow(((MessageBuilder) templateBuilderImpl).mListBuilder);
    }

    public TemplateBuilderImpl createMessageBuilder() {
        return new MessageBuilder(this);
    }

    public void apply(Slice.Builder builder) {
        this.mListBuilder.apply(builder);
    }

    public static final class MessageBuilder extends TemplateBuilderImpl implements MessagingBuilder.MessageBuilder {
        final ListBuilderV1Impl.RowBuilderImpl mListBuilder;

        public MessageBuilder(MessagingListV1Impl messagingListV1Impl) {
            this(messagingListV1Impl.createChildBuilder());
        }

        private MessageBuilder(Slice.Builder builder) {
            super(builder, (SliceSpec) null);
            this.mListBuilder = new ListBuilderV1Impl.RowBuilderImpl(builder);
        }

        public void addSource(Icon icon) {
            this.mListBuilder.setTitleItem(IconCompat.createFromIcon(icon), 1);
        }

        public void addText(CharSequence charSequence) {
            this.mListBuilder.setSubtitle(charSequence);
        }

        public void addTimestamp(long j) {
            this.mListBuilder.addEndItem(j);
        }

        public void apply(Slice.Builder builder) {
            this.mListBuilder.apply(builder);
        }
    }
}

package net.im45.bot.fishpool;

import net.mamoe.mirai.console.command.BlockingCommand;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.JCommandManager;
import net.mamoe.mirai.console.plugins.PluginBase;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Fishpool extends PluginBase {
    public void onEnable() {

        JCommandManager.getInstance().register(this, new BlockingCommand(
                "zrefresh",
                List.of("zr"),
                "Refresh Zapic Pic List",
                "/zr"
        ) {
            @Override
            public boolean onCommandBlocking(@NotNull CommandSender commandSender, @NotNull List<String> list) {
                return false;
            }
        });

        getEventListener().subscribeAlways(GroupMessageEvent.class, e -> {
            Group subject = e.getSubject();
            String msg = e.getMessage()
                    .toString()
                    .replaceFirst("\\[mirai:source:\\d+,\\d+]", "");
            if (msg.isEmpty()) return;
            List<String> msgs = new ArrayList<>(Arrays.asList(msg.split(" ")));
            msgs.removeIf(String::isBlank);

            String cmd = msgs.get(0);
            List<String> args = msgs.subList(1, msgs.size());

            if ("/oh".equals(cmd)) {
                if (args.size() == 0) return;
                int h;
                try {
                    h = Short.parseShort(args.get(0));
                } catch (NumberFormatException x) {
                    subject.sendMessage(x.getMessage());
                    return;
                }
                String hs = "H".repeat(Math.abs(h));
                subject.sendMessage(h > 0 ? "O" + hs : hs + "O");
            } else if ("/na".equals(cmd)) {
                int n;
                if (args.size() == 0) n = 2;
                else try {
                    n = Math.abs(Short.parseShort(args.get(0)));
                } catch (NumberFormatException x) {
                    subject.sendMessage(x.getMessage());
                    return;
                }
                String ns = "呐".repeat(n); // JDK 11+ good.
                subject.sendMessage(ns);
            } else if ("/pa".equals(cmd)) {
                int n;
                if (args.size() == 0) n = 1;
                else try {
                    n = Math.abs(Short.parseShort(args.get(0)));
                } catch (NumberFormatException x) {
                    subject.sendMessage(x.getMessage());
                    return;
                }
                String ns = "爬".repeat(n);
                subject.sendMessage(ns);
            } else if ("/ignore".equals(cmd)) {
                // {Even if no one gives me a fuck, I still will make my voice}
                Image image = MessageUtils.newImage("{6E94879C-ACBF-BC7E-5DD3-1E2D8BB170AA}.mirai");
                subject.sendMessageAsync(image);
            }
        });
    }
}
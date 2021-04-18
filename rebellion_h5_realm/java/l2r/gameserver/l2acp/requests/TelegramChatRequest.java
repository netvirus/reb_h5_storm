package l2r.gameserver.l2acp.requests;

import l2r.gameserver.l2acp.responses.Response;
import com.google.gson.JsonObject;
import l2r.gameserver.model.GameObjectsStorage;
import l2r.gameserver.model.Player;
import l2r.gameserver.network.serverpackets.Say2;
import l2r.gameserver.network.serverpackets.components.ChatType;

/**
 * @author netvirus
 */

public class TelegramChatRequest extends Request {

    private String Text;
    private String Name;

    @Override
    public Response getResponse() {
        Say2 cs = new Say2(0, ChatType.BATTLEFIELD, "[Telegram] " + Name, Text);
        for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
            player.sendPacket(cs);
        }
        return new Response(200, "Successfully announced!");
    }

    @Override
    public void setContent(JsonObject content) {
        super.setContent(content);
        Text = content.get("Message").getAsString();
        Name = content.get("Name").getAsString();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.console.tools;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.provider.wz.WZTool;
import net.sf.odinms.scripting.AbstractPlayerInteraction;
import net.sf.odinms.scripting.npc.NPCConversationManager;
import net.sf.odinms.tools.HexTool;
import net.sf.odinms.tools.data.input.ByteArrayByteStream;
import net.sf.odinms.tools.data.input.GenericSeekableLittleEndianAccessor;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Raz
 */
public class TestClass {

    public static void main(String args[]) {
        //clienterrorpacket();
	  testa();
    }

    public static void clienterrorpacket() {
        byte[] encData = HexTool.getByteArrayFromHexString("43 34 F9 27 C4 0E DD 39 E6 D2 5F 1A 83 30 20 85 21 81 27 F5 5B 68 11 57 D1 61 EC CB D8 9F 6D EB D5 72 69 47 5E 60 2A 89 DE 8D 4E 76 B9 50 96 DE AB 26 3E 22 12 72 6B 12 B5 C7 D3 B6 61 93 AC 53 BB BA 7D 0B 0A 1F 97 82 75 E7 14 3E 78 77 21 2D 0D 7B D9 26 0F FF C1 C1 F4 4A 9F 6E A1 1F A4 3B 25 EA 22 A4 42 7C AD 3B D1 9A 65 A0 84 49 25 6B D8 8C C9 F7 83 C5 0F A9 97 91 47 2D C2 E8 7D 49 21 5F 2E 51 3A FC 81 9D C5 E9 97 36 20 54 92 2F 55 89 C1 9C 24 F0 13 43 7C 25 DF B0 5B B8 31 D4 79 A8 53 67 BC 2C 6C 97 1D A7 24 8F DA DB 91 66 FB DC 2C FE EE 23 92 55 08 07 FF 0A BE B2 14 3B 9D 05 5C CA 11 B9 25 02 DA 45 2B 5B BD 6C 6D FA C0 D3 04 82 B9 C6 7C 30 6A D7 A5 0F 43 A8 79 D0 FB 63 13 9A 66 12 73 47 74 7A 98 ED B9 9E E8 18 FE DA DD B9 5B 5D 1B F8 44 35 F0 0E 93 9E 4C 91 1B F3 A3 46 7C 0C A2 75 8D C1 86 F3 00 DA 71 65 A2 30 6B 4D FE 54 03 63 CD C6 64 B3 89 C5 88 1E FB F7 F7 DD 2E 65 2E 82 99 BD 4F 15 77 67 16 1F 23 EF 21 24 46 E8 6C 93 8E B8 2D 26 26 59 54 5A 84 41 96 AF 19 9E 89 2F 5F 02 43 BF 31 82 51 33 1B C5 7C 42 A1 45 50 6F 58 48 63 31 FF B6 0C 8D 36 95 6E 26 15 08 44 21 99 5F 20 CD 73 11 AD 8A E8 A7 D8 4A 76 54 22 10 0F 1E 2D 3C");
        int length = encData.length;
        byte[] output = new byte[length];
        SeekableLittleEndianAccessor slea = new GenericSeekableLittleEndianAccessor(new ByteArrayByteStream(encData));
        WZTool wzTool = new WZTool(72, WZTool.GMS_IV);


        output = wzTool.decrypt(encData);
        
        System.out.println(HexTool.toStringFromAscii(output));
        System.out.println(HexTool.toString(output));
    }

    public static void testa() {
        int map = 683000100;
	  System.out.println(((map / 100) % 683000));
    }
}

package com.icbc.ipa.shellent.util;

import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author : jing
 * create at:  2020/6/3  7:09 下午
 * @description:
 */
public class StringsUtil {

    public static String getString(String string) {
        return desCbcDecrypt(string);
    }

    public static String t(String string) {
        return encryptToHexString(string);
    }

    private static String encryptToHexString(String content) {
        String s = "$W4f";
        try {
            DESKeySpec keySpec = new DESKeySpec(("azh7" + s).getBytes("utf-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(keySpec.getKey()));
            byte[] result = cipher.doFinal(content.getBytes("utf-8"));
            return byteToHexString(result);
        } catch (Exception e) {
            System.out.println("exception:" + e.toString());
        }
        return null;
    }

    //    public static void main(String[] args) throws IOException {
////        String str = "32a47e0039758264cdef311264cf8aca\u00017250ca7b1a07d911101afe4be32a256e66ee423edfdf182599421fe4db5d0dc7042e50843f23e409e073c8c017b7d309ffd2c6d2a0f4b58a49dc85d348be47c490a817c1437f328ce634cb000a62ac6ad60fc5677743c3a7a3368bb00a711f5b393704f517ebf9c7c255e5c944585698862565d2829a728c50a780e7cbb0d46a1d25765004e3889b95b48f8ac3039088a7966daf37929d5d0e5ca8cb7d110776f7826cf2a5cd27ce3cb77506eec9d9af6dacd49c11e0231b2d11eb73f50d6804ab980c0453197a928c20e88247fdecc5c3cac54f627429f207cda3883a3e02558da77a2f7b7be344f4fa7c1d6a4744951e67114d49e11663ea656b45b02f667dbe70efbb1a73baa08579c2d1ef79d251a1dd296102d8a8e3c44576160294b5392c8821efc03621107251052710a340ead4c41e526ff661eabafd42d9cefa701ee98e6999a898c0768109d2a2bfed05dcfd0d16e92fe50a006dd821b97093d2a083397ae3c107877c8559e8d1625e5c4bc066c683213fde2a48ebf756504c790a642a73b905b6ccc6ee48381e063ac6847f82bb94a1f6f39ebb2e30040e2a3ff77e118535bebdc9c3ba37df4baef758455740da367b25fe68fccd787076501c9b481d874dac71175533b58a14b49868a95f6ed3309c8c989075e0e9eb3dcbbc7de80461f2844cacf257158ec4d0a462a276c3b76f4e11cfef4ecb5a4ba12d83b96e8d25f23857cfc47930a3dc6ce31d87c03772e3f410b21eb5c5ce050864c334445b86c4b43ab52b7843faf591e762f1d69a0ae867ed37199773103b017869dcb185df643717de4cec75ed1cc95c16cbba3f372eb24d61cb4bdc4bf437b715de2daf2f8e3b3dcfe13701f87e10db1314b0a59c3c195cc80bd42db4213936745e34f3c598567cba8bf95805ba9861ddc09bd590a6c4935573acec92f89c49761d4632754e721bdd792f9d5cbad838921f3b526e23835b23ed1084a42e9de167f0f548fb9eb4f89eb38948ab599617013d5f1d475157c0ee768287f4e3dad783a969772f8c9a0f3985cfd33d88d5be5966ee0b918a40bc03872cf5a6298d78888afdf682764c8f56073c139f32af303a515982a0898ff73edbdb05ab57e37f5b1191e3048ebbb1168ff3eea4a99640ac027f0848edd6070198b7d403d070e19442710c4e7768a58d2e2592f953f77f39826282a3ac6f247b63b57252b22fdcff534c3eeb8d205781e4d6fe8a4c82006ffd1630d97d7b1243a41d87fef96470e7166b28d0cadb3427ead95fc35b013488f971488cf325df9fdc220881595577d9f4636b81b8a1f02f8b802a8bddb5bef6e46945b34a0a2df2054d130d7514103882bdea0d7517a27b87c4269db9d1f881a1012b1f81157f223dd1f22911f4cc43772033d0a7ebe2d85599c85608c55cc870d21a4c32d8713cacadf442a2979bb991665cb504c6f0b2cb05462dac964b98dac9c0589030e1d57c2cd4ab76e99968d1f0cb98c31396a5690808156d673c331e3732889afee4838f73d131fa074220e865e1613d31aaa76e3e9f3b6821c98be254b3a80b30619878be0c90d6681713ee80363ef2d0e0a425560ca143e0f366299eb5decd5ab923f13edccb27e46cf3d274899caee1f41a72424be8a5b155f3e2444989eff395f842c5f2c617926e60ac404418c0647267077fbe04b2c379ad0c055b531cc3619920f9a3d94021c8d036a688a3634f9fff175bc55c497b5779f63fce28a401d064e547093867fadf54ffb86f9c4e37a767376301b65fe31c35ccfe9837d35ccf20394736b618b8ce36d15afa31aa2c46c854ea352ae765a1d7a4573ce7e6fad9172de6a04e3ad4765f521e97ff95a130bb35df708e47696d519da86461acc2c649d64f9ef8dfc2208f465667132f547a024dc668df919d69021409c7d54f7ef96b134fed76d7b77c3b8878f43c55664ca5331f541980e54e76d8181200cabab5a2b1f461e334a40b957147dda58e406308daf41b1f0e9b4d77d25ce82d3ea0b3803a19872d02af572fb1d9a814d66922b81394a3077aca74b3a0f0480dd050a9e866f00253ab7f5aaef0fabf591b8903dc2a72c4058ee2ab4b781f1976aaaf33bb78ec1ed98f3d26c1e17a98516b0a4cebce835cb1fe6568c3a0c2df9463b7877c13a6418d31ea5912b1bd4fbb912cd6be74bf707ed8a7d2148c12cdd965ecd310ef49339e86aca0c9e0386cd1a38338a656c74abdf239ea11c16bad7f28e55ee327b33ced93f71e3b7994a75b955bd5eb16caddbeb916417db0646353f3848093313be492900c9475785d422a0386d22357ec0ced9fb0a4c69367083ec1350a249d8fb9accbe1021a2422fe4cea54a3d694e725f89ae50f8f67c3ca857e0efa153a82418a3f32ccef7c3b2c5c0a862f00f4e61d55ac2b0cbf80a161262fff682e2e3ea5330d5b792880f8d57d706dc58e5c79bfbde2c1f1ee07c5f5c28ca655d60cedefaf952af76f82c974553379850b9cfd0f69a1a12cc318f8e6001902aafb0e8451b7e892d9f30cb32df275bb5b0197c28e8f5a062373954dffe9a59f9cdf712e348b9f49bcc84ba7eba62772b5ab68bd00d9bee085d14b0eb5d277241d15290144a52c3f9e146494de1ebda049f3c1106db4750862d9563d773d6ac08f8b5db3e509bfe5d5a162bfc31c98131794154bb71aadf480c809f10e784c26e0cf2a366647f8dd648b5c6056d8766c0f120921af5d5036f2ed45232a09f2267c3e0661c9b50ef7eec4225f445b418d815111db65ea6495d3c1329e8d51368e555131c285e9343a257125bd94d532e7928eb015cbc55d78787a5789410c3e2e9584ac7b2be54846de446765b8f92c6b05c233837ea0d3e71792524935156fa17078d232c9415cefb7fef8b7b5b343e41c6a723cf96c87b81c4f52f9f214aa11998e4fd85fadc2d55a49312a0d4c9c47c2dfa51a352f2b7015dfc9608eaa50a67d9fdcad1e2de0665c3c5408d209c841f30af9396e7c688badf7eb0d045f0d33945f585d55804192aa25993754cebae57545c228eb57c1efcf9c3be22964d15424adebc010251b1d292569e14b6cb3241fb8a383d203c474bab3ddf11e0922bdc26f91bf64a3778067dab270da490ca2486b737e645bf096797d33fe831c660f3501fd769d8d47041e331e53bb5d9999e2d5c996a19b0c055ce963d13c027c16db64e1707ae52e458dd7829d7491f6eda844987ee5c37908f07b4a3e3a634b5c34069d6d4f2ef4e56662a43e4d711d5d742f4e41b76b922cb34eb0306388ec89b2882473a20af5647a3c1f62ed221e26c54e820f71f0db0b6e4616953b956549f5f8003c55698424c1434b91472311ac3279a32a2b47cabe5ab3c039a13d2198eb6afe317b9e2eb438bf32b0a14ed2365197eefad7d78b0963a5168d625586a20c5fda9b5e16e33d3f9ef9bde0bc71b697ea8283998c50bb68353a86364e4cacbc49720de3e3460e9ada3f2dc4a9e4daa8b767b9a82f8bb4cb0cdea2e2bb8b09c27aa755918fd2630173fbfb624ae537133976d49395a5d7b47f9aaaa1090355aa302e3ee2dae8bab72ddf7d230d264983d23ab326e788e55b01b3ede40546e022809966c7267d6cba2f5b2f06ae39637a584cf36e5615dd7986214221bf7564ce850db1560c5de895441f4fef84931e6cbfca09d91a3070eb6b1e02bac6790406e0505946f428530631448370e63a8cd71f94fd0db8e368283322993df75c729ad26fca414f6301cafa50f3f5686e60cf48c19e12797a228f9a7721854ab59e9056923609ab877792ab6a8cc572c3b63dfdeac732fed5c35c48606141f0eb98cd7e32f215bee0a8fef9d35d1cdca6b46dbe6b01d8d1359992b03b8414be7ed6888371ebdcb5a8649f6367b08a7f34ff322e7ff3fedd8ef10da1cc04f28b9316f3ca751b5b02c12b1e5becaf153e864a8bc4f5f76a27d90e61ab1b116cd8e4548f2b0fa8a4aad9bc38be5afd645a343d12ca129eb854ef0b318e6a330b3b5cf9db6bb3bb00f23631685b8a6475bdaa0a823a06658ebdd1a8786a71c36637ac5653fe6b9728de6f59ade1b9977e8168390d8513bc829c4336e43f4553f2502ce1340065cc70d7ae852f86bc3949646a88880c2ed71d1b5c99328bdaf1b673120075372283a26db0d5216d374f69dcb7bfc7295bafb0568a3e182fc941556fecf71175268d85272938fe5b5312fa94fae17c0ae6b973694e0d102ce5985e3d06f163f8d91202e2ac8d8f174a95dfd8f94c449a0ddbe329c661a87770d1e5e07e8a275ac176e4daec01b7884601ebbaf1e9024d2fd7b3d2a580ea7700a601bc18c5dcc3c6564bed801caefec8722a56e604247c22cb6843bd64a3a31f8fccac352f77af8dc262f5956e302d9b23b8bf07b99c294ac2c2e3f59d766abb30ca121298b80c9624f5124cb3027dd83d7ea3ff3ae3c9c58065c2c5debacf69e9c543473b784f574ed41df56fb8b400d6ed553197ce5303e498481762f6784066a73799ab9d2d980e665933fab5893d17d8735f9804ab509759044c082ef7028a369aba4c1d166f3875493b3a66af5f3524fa655bd1e16086a3752f86281c6943580ab973976bbecfe9870d934b2cc703bc39f9f8e35e69dd55a6e3397a18d9b016480d41d1a4ba1b6b7019e09573612e8df4a0b5a441e3826d6de67872d62d9227bc71ec2d401f64356415d8acc51eda79d7ea5c1ba45c2acb3524f25e03724d720e2ed8c0d248e7f5b0098d856487a99197d402212e7eff014d5b4d152a80f9faaa170c7b47beea65ff7cade79a70a6bdb47994f802b6b143291f473712b87071bbe85210360c861d6f102168659626bce7a572f36e6fb2a557e6be90157b9bf75fc51586f0d574e7b203d91ed57a25f914f83339a0e463e6a28a1082920f9192c91e3509887091d5f7ec351e5c92fc0420405f74117b0948342450684422e77813dbe047e468a806a12b69bd8986057936743ad932dc35c746c65a214ef238db50ca144c29be7769b48ec1462f2e1408e63007f4e60f4c68714c300d9852";
//        String str = "7250ca7b1a07d911101afe4be32a256e66ee423edfdf182599421fe4db5d0dc7042e50843f23e409e073c8c017b7d309ffd2c6d2a0f4b58a49dc85d348be47c490a817c1437f328ce634cb000a62ac6ad60fc5677743c3a7a3368bb00a711f5b393704f517ebf9c7c255e5c944585698862565d2829a728c50a780e7cbb0d46a1d25765004e3889b95b48f8ac3039088a7966daf37929d5d0e5ca8cb7d110776f7826cf2a5cd27ce3cb77506eec9d9af6dacd49c11e0231b2d11eb73f50d6804ab980c0453197a928c20e88247fdecc5c3cac54f627429f207cda3883a3e02558da77a2f7b7be344f4fa7c1d6a4744951e67114d49e11663ea656b45b02f667dbe70efbb1a73baa08579c2d1ef79d251a1dd296102d8a8e3c44576160294b5392c8821efc03621107251052710a340ead4c41e526ff661eabafd42d9cefa701ee98e6999a898c0768109d2a2bfed05dcfd0d16e92fe50a006dd821b97093d2a083397ae3c107877c8559e8d1625e5c4bc066c683213fde2a48ebf756504c790a642a73b905b6ccc6ee48381e063ac6847f82bb94a1f6f39ebb2e30040e2a3ff77e118535bebdc9c3ba37df4baef758455740da367b25fe68fccd787076501c9b481d874dac71175533b58a14b49868a95f6ed3309c8c989075e0e9eb3dcbbc7de80461f2844cacf257158ec4d0a462a276c3b76f4e11cfef4ecb5a4ba12d83b96e8d25f23857cfc47930a3dc6ce31d87c03772e3f410b21eb5c5ce050864c334445b86c4b43ab52b7843faf591e762f1d69a0ae867ed37199773103b017869dcb185df643717de4cec75ed1cc95c16cbba3f372eb24d61cb4bdc4bf437b715de2daf2f8e3b3dcfe13701f87e10db1314b0a59c3c195cc80bd42db4213936745e34f3c598567cba8bf95805ba9861ddc09bd590a6c4935573acec92f89c49761d4632754e721bdd792f9d5cbad838921f3b526e23835b23ed1084a42e9de167f0f548fb9eb4f89eb38948ab599617013d5f1d475157c0ee768287f4e3dad783a969772f8c9a0f3985cfd33d88d5be5966ee0b918a40bc03872cf5a6298d78888afdf682764c8f56073c139f32af303a515982a0898ff73edbdb05ab57e37f5b1191e3048ebbb1168ff3eea4a99640ac027f0848edd6070198b7d403d070e19442710c4e7768a58d2e2592f953f77f39826282a3ac6f247b63b57252b22fdcff534c3eeb8d205781e4d6fe8a4c82006ffd1630d97d7b1243a41d87fef96470e7166b28d0cadb3427ead95fc35b013488f971488cf325df9fdc220881595577d9f4636b81b8a1f02f8b802a8bddb5bef6e46945b34a0a2df2054d130d7514103882bdea0d7517a27b87c4269db9d1f881a1012b1f81157f223dd1f22911f4cc43772033d0a7ebe2d85599c85608c55cc870d21a4c32d8713cacadf442a2979bb991665cb504c6f0b2cb05462dac964b98dac9c0589030e1d57c2cd4ab76e99968d1f0cb98c31396a5690808156d673c331e3732889afee4838f73d131fa074220e865e1613d31aaa76e3e9f3b6821c98be254b3a80b30619878be0c90d6681713ee80363ef2d0e0a425560ca143e0f366299eb5decd5ab923f13edccb27e46cf3d274899caee1f41a72424be8a5b155f3e2444989eff395f842c5f2c617926e60ac404418c0647267077fbe04b2c379ad0c055b531cc3619920f9a3d94021c8d036a688a3634f9fff175bc55c497b5779f63fce28a401d064e547093867fadf54ffb86f9c4e37a767376301b65fe31c35ccfe9837d35ccf20394736b618b8ce36d15afa31aa2c46c854ea352ae765a1d7a4573ce7e6fad9172de6a04e3ad4765f521e97ff95a130bb35df708e47696d519da86461acc2c649d64f9ef8dfc2208f465667132f547a024dc668df919d69021409c7d54f7ef96b134fed76d7b77c3b8878f43c55664ca5331f541980e54e76d8181200cabab5a2b1f461e334a40b957147dda58e406308daf41b1f0e9b4d77d25ce82d3ea0b3803a19872d02af572fb1d9a814d66922b81394a3077aca74b3a0f0480dd050a9e866f00253ab7f5aaef0fabf591b8903dc2a72c4058ee2ab4b781f1976aaaf33bb78ec1ed98f3d26c1e17a98516b0a4cebce835cb1fe6568c3a0c2df9463b7877c13a6418d31ea5912b1bd4fbb912cd6be74bf707ed8a7d2148c12cdd965ecd310ef49339e86aca0c9e0386cd1a38338a656c74abdf239ea11c16bad7f28e55ee327b33ced93f71e3b7994a75b955bd5eb16caddbeb916417db0646353f3848093313be492900c9475785d422a0386d22357ec0ced9fb0a4c69367083ec1350a249d8fb9accbe1021a2422fe4cea54a3d694e725f89ae50f8f67c3ca857e0efa153a82418a3f32ccef7c3b2c5c0a862f00f4e61d55ac2b0cbf80a161262fff682e2e3ea5330d5b792880f8d57d706dc58e5c79bfbde2c1f1ee07c5f5c28ca655d60cedefaf952af76f82c974553379850b9cfd0f69a1a12cc318f8e6001902aafb0e8451b7e892d9f30cb32df275bb5b0197c28e8f5a062373954dffe9a59f9cdf712e348b9f49bcc84ba7eba62772b5ab68bd00d9bee085d14b0eb5d277241d15290144a52c3f9e146494de1ebda049f3c1106db4750862d9563d773d6ac08f8b5db3e509bfe5d5a162bfc31c98131794154bb71aadf480c809f10e784c26e0cf2a366647f8dd648b5c6056d8766c0f120921af5d5036f2ed45232a09f2267c3e0661c9b50ef7eec4225f445b418d815111db65ea6495d3c1329e8d51368e555131c285e9343a257125bd94d532e7928eb015cbc55d78787a5789410c3e2e9584ac7b2be54846de446765b8f92c6b05c233837ea0d3e71792524935156fa17078d232c9415cefb7fef8b7b5b343e41c6a723cf96c87b81c4f52f9f214aa11998e4fd85fadc2d55a49312a0d4c9c47c2dfa51a352f2b7015dfc9608eaa50a67d9fdcad1e2de0665c3c5408d209c841f30af9396e7c688badf7eb0d045f0d33945f585d55804192aa25993754cebae57545c228eb57c1efcf9c3be22964d15424adebc010251b1d292569e14b6cb3241fb8a383d203c474bab3ddf11e0922bdc26f91bf64a3778067dab270da490ca2486b737e645bf096797d33fe831c660f3501fd769d8d47041e331e53bb5d9999e2d5c996a19b0c055ce963d13c027c16db64e1707ae52e458dd7829d7491f6eda844987ee5c37908f07b4a3e3a634b5c34069d6d4f2ef4e56662a43e4d711d5d742f4e41b76b922cb34eb0306388ec89b2882473a20af5647a3c1f62ed221e26c54e820f71f0db0b6e4616953b956549f5f8003c55698424c1434b91472311ac3279a32a2b47cabe5ab3c039a13d2198eb6afe317b9e2eb438bf32b0a14ed2365197eefad7d78b0963a5168d625586a20c5fda9b5e16e33d3f9ef9bde0bc71b697ea8283998c50bb68353a86364e4cacbc49720de3e3460e9ada3f2dc4a9e4daa8b767b9a82f8bb4cb0cdea2e2bb8b09c27aa755918fd2630173fbfb624ae537133976d49395a5d7b47f9aaaa1090355aa302e3ee2dae8bab72ddf7d230d264983d23ab326e788e55b01b3ede40546e022809966c7267d6cba2f5b2f06ae39637a584cf36e5615dd7986214221bf7564ce850db1560c5de895441f4fef84931e6cbfca09d91a3070eb6b1e02bac6790406e0505946f428530631448370e63a8cd71f94fd0db8e368283322993df75c729ad26fca414f6301cafa50f3f5686e60cf48c19e12797a228f9a7721854ab59e9056923609ab877792ab6a8cc572c3b63dfdeac732fed5c35c48606141f0eb98cd7e32f215bee0a8fef9d35d1cdca6b46dbe6b01d8d1359992b03b8414be7ed6888371ebdcb5a8649f6367b08a7f34ff322e7ff3fedd8ef10da1cc04f28b9316f3ca751b5b02c12b1e5becaf153e864a8bc4f5f76a27d90e61ab1b116cd8e4548f2b0fa8a4aad9bc38be5afd645a343d12ca129eb854ef0b318e6a330b3b5cf9db6bb3bb00f23631685b8a6475bdaa0a823a06658ebdd1a8786a71c36637ac5653fe6b9728de6f59ade1b9977e8168390d8513bc829c4336e43f4553f2502ce1340065cc70d7ae852f86bc3949646a88880c2ed71d1b5c99328bdaf1b673120075372283a26db0d5216d374f69dcb7bfc7295bafb0568a3e182fc941556fecf71175268d85272938fe5b5312fa94fae17c0ae6b973694e0d102ce5985e3d06f163f8d91202e2ac8d8f174a95dfd8f94c449a0ddbe329c661a87770d1e5e07e8a275ac176e4daec01b7884601ebbaf1e9024d2fd7b3d2a580ea7700a601bc18c5dcc3c6564bed801caefec8722a56e604247c22cb6843bd64a3a31f8fccac352f77af8dc262f5956e302d9b23b8bf07b99c294ac2c2e3f59d766abb30ca121298b80c9624f5124cb3027dd83d7ea3ff3ae3c9c58065c2c5debacf69e9c543473b784f574ed41df56fb8b400d6ed553197ce5303e498481762f6784066a73799ab9d2d980e665933fab5893d17d8735f9804ab509759044c082ef7028a369aba4c1d166f3875493b3a66af5f3524fa655bd1e16086a3752f86281c6943580ab973976bbecfe9870d934b2cc703bc39f9f8e35e69dd55a6e3397a18d9b016480d41d1a4ba1b6b7019e09573612e8df4a0b5a441e3826d6de67872d62d9227bc71ec2d401f64356415d8acc51eda79d7ea5c1ba45c2acb3524f25e03724d720e2ed8c0d248e7f5b0098d856487a99197d402212e7eff014d5b4d152a80f9faaa170c7b47beea65ff7cade79a70a6bdb47994f802b6b143291f473712b87071bbe85210360c861d6f102168659626bce7a572f36e6fb2a557e6be90157b9bf75fc51586f0d574e7b203d91ed57a25f914f83339a0e463e6a28a1082920f9192c91e3509887091d5f7ec351e5c92fc0420405f74117b0948342450684422e77813dbe047e468a806a12b69bd8986057936743ad932dc35c746c65a214ef238db50ca144c29be7769b48ec1462f2e1408e63007f4e60f4c68714c300d9852";
////        String str="ec3bf5908f37393ab7992b4cd73367ca3b4da150022234a200db52238ca10b6a6c8417a35121a6a9e3bcd88ad6318258f64bb3fa72c073d82cd57dad9dc425de565074732eaba806e01c7ae2c3c51ad8800f49ad00f37fc61d9c2150ae8508d675920505e84fefc8518619e6494741ba9e85a4886bbe55db6636933bab16c610ccf298a68c1d528008670ec5f785725cc356e21567fd7f0b9a6c69de2d131e315f416676ae3128788024effcc1a8e0c9a2ef548dd4d99da0c81957f8ace756629f43eaefb6f38ed9dd6b842ae949b40a570851ddb17ac0b4e63dbba4091bb4d4f8b6f98b8be6ed566a88fe9919129deaec6593bfa83fe5f6eaf19818c225d6fc94";
//        System.out.println(desCbcDecrypt(str));
//
//        String zipPath = "D:/shellent_20200602_SHELLCOMPANY_00.zip";
//        FileInputStream fis = new FileInputStream(new File(zipPath));
//        String md5 = DigestUtils.md5Hex(fis);
//        System.out.println(md5);
//
//        String okMD5 = "beb11b6d34bece559f5a614ae04b7dff";
//
//        System.out.println("md5 校验结果：" + md5.equals(okMD5));
//
//    }
    public static String desCbcDecrypt(String hexContent) {
        try {
            byte[] keyBytes = "azh7$W4f".getBytes("utf-8");
            DESKeySpec keySpec = new DESKeySpec(keyBytes);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(keyBytes));
            byte[] result = cipher.doFinal(hexToByteArray(hexContent));
            return new String(result, "utf-8");
        } catch (Exception e) {
            System.out.println("exception:" + e.toString());
        }
        return null;
    }

    private static String byteToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length);
        String sTemp;
        for (byte aByte : bytes) {
            sTemp = Integer.toHexString(0xFF & aByte);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp);
        }
        return sb.toString();
    }


    /**
     * hex字符串转byte数组
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte数组结果
     */
    private static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            // 奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            // 偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }


    /**
     * Hex字符串转byte
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte
     */
    private static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

}

package com.finebi.cube.tools;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.base.TableData;
import com.fr.bi.base.BICore;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.data.db.*;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.data.source.SourceFile;
import com.fr.bi.stable.utils.algorithem.BIRandomUitils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.*;

/**
 * This class created on 2016/4/6.
 *
 * @author Connery
 * @since 4.0
 */
public class BIMemDataSourceTestToolCube implements CubeTableSource {
    public List<String> stringData = new ArrayList<String>();
    public List<Long> dateData = new ArrayList<Long>();
    public List<Double> floatData = new ArrayList<Double>();
    public List<Long> longData = new ArrayList<Long>();
    public List<Double> doubleData = new ArrayList<Double>();
    public static int rowNumber = 100;

    public int getRowNumber() {
        return rowNumber;
    }

    public BIMemDataSourceTestToolCube() {

        initialStatic();

    }

    private void initialRandom() {
        for (int i = 0; i < rowNumber; i++) {
            stringData.add(BIRandomUitils.getRandomCharacterString(10));
            dateData.add(BIRandomUitils.getRandomLong());
            floatData.add(BIRandomUitils.getRandomDouble());
            longData.add(BIRandomUitils.getRandomLong());
            doubleData.add(BIRandomUitils.getRandomDouble());

        }
    }

    @Override
    public boolean isIndependent() {
        return true;
    }

    @Override
    public Set<CubeTableSource> getSourceUsedBaseSource() {
        Set<CubeTableSource> set = new HashSet<CubeTableSource>();
        set.add(this);
        return set;
    }

    private void initialStatic() {
        stringData.add("玖二");
        stringData.add("七玖");
        stringData.add("贰拾");
        stringData.add("私额");
        stringData.add("五风");
        stringData.add("亿万");
        stringData.add("另啵");
        stringData.add("贰二");
        stringData.add("捌万");
        stringData.add("元方");
        stringData.add("元四");
        stringData.add("叁一");
        stringData.add("八贰");
        stringData.add("角洒");
        stringData.add("贰方");
        stringData.add("拾元");
        stringData.add("元六");
        stringData.add("另万");
        stringData.add("叁元");
        stringData.add("里仟");
        stringData.add("另九");
        stringData.add("捌十");
        stringData.add("的柒");
        stringData.add("念捌");
        stringData.add("正啊");
        stringData.add("师啵");
        stringData.add("万二");
        stringData.add("零万");
        stringData.add("五方");
        stringData.add("仟司");
        stringData.add("亿一");
        stringData.add("啊噗");
        stringData.add("二整");
        stringData.add("捌一");
        stringData.add("元柒");
        stringData.add("六啵");
        stringData.add("万角");
        stringData.add("正贰");
        stringData.add("啵法");
        stringData.add("里另");
        stringData.add("万机");
        stringData.add("方的");
        stringData.add("风法");
        stringData.add("里伍");
        stringData.add("八壹");
        stringData.add("亿叁");
        stringData.add("二整");
        stringData.add("七十");
        stringData.add("飞角");
        stringData.add("一德");
        stringData.add("玖机");
        stringData.add("师师");
        stringData.add("另一");
        stringData.add("捌二");
        stringData.add("方里");
        stringData.add("格风");
        stringData.add("柒阿");
        stringData.add("五四");
        stringData.add("念角");
        stringData.add("叁飞");
        stringData.add("阿洒");
        stringData.add("六四");
        stringData.add("贰五");
        stringData.add("私贰");
        stringData.add("五叁");
        stringData.add("柒一");
        stringData.add("师分");
        stringData.add("十零");
        stringData.add("毛陆");
        stringData.add("格飞");
        stringData.add("柒另");
        stringData.add("念啊");
        stringData.add("整佰");
        stringData.add("正陆");
        stringData.add("捌毛");
        stringData.add("另另");
        stringData.add("壹正");
        stringData.add("柒佰");
        stringData.add("亿德");
        stringData.add("司洒");
        stringData.add("格四");
        stringData.add("捌飞");
        stringData.add("的两");
        stringData.add("念三");
        stringData.add("阿佰");
        stringData.add("师壹");
        stringData.add("万格");
        stringData.add("格阿");
        stringData.add("五元");
        stringData.add("仟角");
        stringData.add("方噗");
        stringData.add("四机");
        stringData.add("噗十");
        stringData.add("洒一");
        stringData.add("二师");
        stringData.add("玖的");
        stringData.add("飞二");
        stringData.add("一两");
        stringData.add("万仟");
        stringData.add("壹格");
        floatData.add(0.39642986233329713);
        floatData.add(0.3958923717000673);
        floatData.add(0.3975048435997568);
        floatData.add(0.3940111693849241);
        floatData.add(0.3927570245740545);
        floatData.add(0.39508615065138375);
        floatData.add(0.4138982185062995);
        floatData.add(0.41336074277423085);
        floatData.add(0.41497318487159796);
        floatData.add(0.4144357091395293);
        floatData.add(0.4103149625192618);
        floatData.add(0.4126440736954299);
        floatData.add(0.4116586891023364);
        floatData.add(0.40753794248206887);
        floatData.add(0.40986705365823695);
        floatData.add(0.40861290884736734);
        floatData.add(0.4051192495336958);
        floatData.add(0.4067317214333853);
        floatData.add(0.4057463219391306);
        floatData.add(0.42527504397168614);
        floatData.add(0.42688751587137563);
        floatData.add(0.4263500252381458);
        floatData.add(0.4222292786178783);
        floatData.add(0.42169180288580965);
        floatData.add(0.42330425988433795);
        floatData.add(0.4199001674417102);
        floatData.add(0.4186460375320018);
        floatData.add(0.4209751338070087);
        floatData.add(0.41685440208790236);
        floatData.add(0.4163169263558337);
        floatData.add(0.41792936845320083);
        floatData.add(0.4173918927211322);
        floatData.add(0.43611439370500427);
        floatData.add(0.43844350488117234);
        floatData.add(0.43718936007030273);
        floatData.add(0.4337852825288362);
        floatData.add(0.4353977395273645);
        floatData.add(0.43486024889413466);
        floatData.add(0.43073951717502823);
        floatData.add(0.4302020265417984);
        floatData.add(0.4318144835403268);
        floatData.add(0.42841040599886016);
        floatData.add(0.42715626118799055);
        floatData.add(0.4294853723641586);
        floatData.add(0.35656616741718006);
        floatData.add(0.3560286767839502);
        floatData.add(0.3576411337824785);
        floatData.add(0.3571036580504099);
        floatData.add(0.3529829114301424);
        floatData.add(0.35531202260631045);
        floatData.add(0.35405789269660204);
        floatData.add(0.3506538002539743);
        floatData.add(0.3522662572525026);
        floatData.add(0.35172878152043396);
        floatData.add(0.34733928958355154);
        floatData.add(0.3496684007597196);
        floatData.add(0.34832467417664503);
        floatData.add(0.36785341111036185);
        floatData.add(0.36946588301005134);
        floatData.add(0.3689283923768215);
        floatData.add(0.364807645756554);
        floatData.add(0.36427015512332417);
        floatData.add(0.36588262702301366);
        floatData.add(0.3624785345803859);
        floatData.add(0.3612243897695163);
        floatData.add(0.3635535158468456);
        floatData.add(0.35943276922657796);
        floatData.add(0.35889527859334813);
        floatData.add(0.36050775049303774);
        floatData.add(0.3599702598598078);
        floatData.add(0.37878234261588484);
        floatData.add(0.3811114537920529);
        floatData.add(0.37618448612310185);
        floatData.add(0.3777073613494252);
        floatData.add(0.37716987071619534);
        floatData.add(0.373049138997089);
        floatData.add(0.3753782501732571);
        floatData.add(0.3741241053623875);
        floatData.add(0.3702721040587349);
        floatData.add(0.3725116483638592);
        floatData.add(0.3712575035529896);
        floatData.add(0.3907862404867063);
        floatData.add(0.3923987123863958);
        floatData.add(0.39186120685200476);
        floatData.add(0.38774047513289844);
        floatData.add(0.3872029844996686);
        floatData.add(0.3888154414981969);
        floatData.add(0.38541136395673037);
        floatData.add(0.38415721914586076);
        floatData.add(0.38648633032202884);
        floatData.add(0.3823655986029225);
        floatData.add(0.3818281079696927);
        floatData.add(0.383440564968221);
        floatData.add(0.38290307433499116);
        floatData.add(0.3099838693880126);
        floatData.add(0.31231298056418066);
        floatData.add(0.31105883575331106);
        floatData.add(0.3076547582118445);
        floatData.add(0.3092672152103728);
        floatData.add(0.308729724577143);
        longData.add(5612434196993630086L);
        longData.add(5655398777807286599L);
        longData.add(5632264004183774215L);
        longData.add(5569469611885006277L);
        longData.add(5599214322670222470L);
        longData.add(5589299423370117701L);
        longData.add(5936321049197639950L);
        longData.add(5926406149897535182L);
        longData.add(5956150856387784078L);
        longData.add(5878484115138858989L);
        longData.add(5842129467191938988L);
        longData.add(5818994689273459307L);
        longData.add(5861959274382083116L);
        longData.add(5785945012054874858L);
        longData.add(5776030108459802794L);
        longData.add(5805774819245018987L);
        longData.add(5795859915649946923L);
        longData.add(6184193643369408852L);
        longData.add(6161058869745896468L);
        longData.add(6098264477447128530L);
        longData.add(6128009188232344723L);
        longData.add(6118094284637272658L);
        longData.add(6042080022310064401L);
        longData.add(6032165118714992336L);
        longData.add(6061909833795175825L);
        longData.add(5990853021118019951L);
        longData.add(6027207669064939952L);
        longData.add(6002420407929776144L);
        longData.add(6362661908080706008L);
        longData.add(6392406618865922201L);
        longData.add(6382491715270850137L);
        longData.add(6306477452943641879L);
        longData.add(6296562549348569815L);
        longData.add(6326307264428753304L);
        longData.add(6245335543861558070L);
        longData.add(6275080258941741558L);
        longData.add(6228810707399749493L);
        longData.add(6218895803804677429L);
        longData.add(-6804395969040716678L);
        longData.add(-6761431383932092869L);
        longData.add(-6784566161850572549L);
        longData.add(-6847360549854373191L);
        longData.add(-6817615839069156998L);
        longData.add(-6827530742664229062L);
        longData.add(-6901892521774753192L);
        longData.add(-6872147810989536999L);
        longData.add(-6882062710289641768L);
        longData.add(-6958076976911817321L);
        longData.add(-6967991876211922090L);
        longData.add(-6938247169721673193L);
        longData.add(-6578005669570743328L);
        longData.add(-6601140443194255713L);
        longData.add(-6566438278464019840L);
        longData.add(-6629232666467820481L);
        longData.add(-6652367444386300162L);
        longData.add(-6609402863572643649L);
        longData.add(-6696984508416640803L);
        longData.add(-6667239801926391906L);
        longData.add(-6677154705521463970L);
        longData.add(-6753168967848672228L);
        longData.add(-6710204387035015715L);
        longData.add(-6733339156363560804L);
        longData.add(-6373097660507598235L);
        longData.add(-6343352949722382042L);
        longData.add(-6353267849022486810L);
        longData.add(-6434239565294714748L);
        longData.add(-6391274984481058235L);
        longData.add(-6414409753809603324L);
        longData.add(-6477204150403338558L);
        longData.add(-6447459435323155069L);
        longData.add(-6457374338918227133L);
        longData.add(-6533388601245435391L);
        longData.add(-6543303504840507455L);
        longData.add(-6513558794055291262L);
        longData.add(-6153317293904361398L);
        longData.add(-6176452071822841078L);
        longData.add(-6133487486714217269L);
        longData.add(-6209501749041425527L);
        longData.add(-6219416652636497591L);
        longData.add(-6189671941851281398L);
        longData.add(-6199586841151386167L);
        longData.add(-6275601107773561721L);
        longData.add(-6232636522664937912L);
        longData.add(-6255771300583417592L);
        longData.add(-6318565688587218234L);
        longData.add(-6288820977802002041L);
        longData.add(-6298735881397074105L);
        longData.add(-7643857812483441178L);
        longData.add(-7653772711783545946L);
        longData.add(-7625680488509981178L);
        longData.add(-7688474876513781819L);
        longData.add(-7711609654432261500L);
        longData.add(-7668645073618604987L);
        longData.add(-7744659331650845949L);
        longData.add(-7754574235245918013L);
        longData.add(-7724829524460701820L);
        longData.add(-7734744428055773884L);
        longData.add(-7805801236437962462L);
        longData.add(-7776056525652746270L);
        longData.add(-7785971433542785630L);
        doubleData.add(0.5967337215069758);
        doubleData.add(0.5989732509109389);
        doubleData.add(0.5979878663178454);
        doubleData.add(0.5938671196975779);
        doubleData.add(0.5961962457749072);
        doubleData.add(0.5949421009640375);
        doubleData.add(0.5915380085214098);
        doubleData.add(0.5931504804210993);
        doubleData.add(0.5926129897878695);
        doubleData.add(0.588402661395397);
        doubleData.add(0.5878651707621672);
        doubleData.add(0.5894776426618566);
        doubleData.add(0.6090063646944122);
        doubleData.add(0.6077522198835426);
        doubleData.add(0.6100813310597106);
        doubleData.add(0.6059605993406043);
        doubleData.add(0.6054231087073745);
        doubleData.add(0.6070355657059028);
        doubleData.add(0.6064980899738341);
        doubleData.add(0.6023773433535666);
        doubleData.add(0.6047064545297347);
        doubleData.add(0.6034523246200263);
        doubleData.add(0.6000482321773986);
        doubleData.add(0.6016606891759269);
        doubleData.add(0.6011232134438582);
        doubleData.add(0.619935281298774);
        doubleData.add(0.6193977906655441);
        doubleData.add(0.6210102625652336);
        doubleData.add(0.6176061701226059);
        doubleData.add(0.6163520253117363);
        doubleData.add(0.6182332425280407);
        doubleData.add(0.614829150085413);
        doubleData.add(0.6135750201757045);
        doubleData.add(0.6158145495796676);
        doubleData.add(0.6116938029594001);
        doubleData.add(0.6111563272273315);
        doubleData.add(0.6127687693246986);
        doubleData.add(0.61223129359263);
        doubleData.add(0.5393120886456514);
        doubleData.add(0.5416411998218195);
        doubleData.add(0.5403870550109499);
        doubleData.add(0.5365350686084585);
        doubleData.add(0.5388641946857877);
        doubleData.add(0.5376100498749181);
        doubleData.add(0.5342059574322904);
        doubleData.add(0.5357288326586137);
        doubleData.add(0.534743433164359);
        doubleData.add(0.5313393556228925);
        doubleData.add(0.5329518126214208);
        doubleData.add(0.5324143219881909);
        doubleData.add(0.5512264047442679);
        doubleData.add(0.5505993323388331);
        doubleData.add(0.5522118042385226);
        doubleData.add(0.5488076968947336);
        doubleData.add(0.5475535818861864);
        doubleData.add(0.5498826930623545);
        doubleData.add(0.545761946442087);
        doubleData.add(0.5452244558088571);
        doubleData.add(0.5471056581240004);
        doubleData.add(0.542984926404894);
        doubleData.add(0.5424474357716642);
        doubleData.add(0.5440598927701925);
        doubleData.add(0.5435224170381239);
        doubleData.add(0.5623344848930396);
        doubleData.add(0.5645740291981639);
        doubleData.add(0.5635886297039092);
        doubleData.add(0.5594678830836417);
        doubleData.add(0.5617969942598098);
        doubleData.add(0.5605428643501014);
        doubleData.add(0.5571387719074736);
        doubleData.add(0.5587512289060019);
        doubleData.add(0.5582137531739333);
        doubleData.add(0.5540930065536658);
        doubleData.add(0.5535555308215971);
        doubleData.add(0.5551679878201254);
        doubleData.add(0.5746967098526811);
        doubleData.add(0.5734425650418115);
        doubleData.add(0.5757716911191407);
        doubleData.add(0.5716509444988731);
        doubleData.add(0.5711134538656433);
        doubleData.add(0.5726363439931279);
        doubleData.add(0.5720988533598981);
        doubleData.add(0.5679781216407916);
        doubleData.add(0.5703072328169597);
        doubleData.add(0.5690530880060901);
        doubleData.add(0.5656489955634624);
        doubleData.add(0.5672614674631519);
        doubleData.add(0.566723976829922);
        doubleData.add(0.49380477188294347);
        doubleData.add(0.49326728124971364);
        doubleData.add(0.49487973824824194);
        doubleData.add(0.49102775184575054);
        doubleData.add(0.4904902612125207);
        doubleData.add(0.492102718211049);
        doubleData.add(0.4886090588973775);
        doubleData.add(0.4873549140865079);
        doubleData.add(0.48968402526267596);
        doubleData.add(0.48556327864240845);
        doubleData.add(0.4850258029103398);
        doubleData.add(0.4869070201266442);
        dateData.add(9117873486010777791L);
        dateData.add(9127788389605849855L);
        dateData.add(9098043678820633662L);
        dateData.add(9107958586710673022L);
        dateData.add(9183972844742913984L);
        dateData.add(9141008263929257471L);
        dateData.add(9164143037552769856L);
        dateData.add(9219806648152981119L);
        dateData.add(9197192714771354304L);
        dateData.add(9208760101583110497L);
        dateData.add(9161969705504265565L);
        dateData.add(9152054806204160797L);
        dateData.add(9181799516989376990L);
        dateData.add(8904703060864212057L);
        dateData.add(8922880384837672058L);
        dateData.add(8893135674052455865L);
        dateData.add(8955930062056256507L);
        dateData.add(8979064835679768891L);
        dateData.add(8937752738082796506L);
        dateData.add(9013767000410004764L);
        dateData.add(9023681904005076828L);
        dateData.add(8993937188924893339L);
        dateData.add(9003852096814932700L);
        dateData.add(9079866354847173662L);
        dateData.add(9036901774033517149L);
        dateData.add(9060036547657029533L);
        dateData.add(8708057472179454900L);
        dateData.add(8665092887070831091L);
        dateData.add(8688227664989310772L);
        dateData.add(8751022052993111413L);
        dateData.add(8722929825424579349L);
        dateData.add(8732844729019651413L);
        dateData.add(8808858991346859671L);
        dateData.add(8774156830911591094L);
        dateData.add(8784071730211695862L);
        dateData.add(8860085996833871416L);
        dateData.add(8870000896133976184L);
        dateData.add(8841908672860411416L);
        dateData.add(3405236493377878994L);
        dateData.add(3428371271296358675L);
        dateData.add(3393669110861090098L);
        dateData.add(3458115982081574867L);
        dateData.add(3476293306055034868L);
        dateData.add(3446548595269818675L);
        dateData.add(3509342983273619317L);
        dateData.add(3534130244408783125L);
        dateData.add(3491165659300159316L);
        dateData.add(3562222471977315190L);
        dateData.add(3585357245600827574L);
        dateData.add(3542392664787171061L);
        dateData.add(3195371034664681517L);
        dateData.add(3205285938259753581L);
        dateData.add(3175541227474537388L);
        dateData.add(3185456135364576749L);
        dateData.add(3261470393396817710L);
        dateData.add(3218505812583161197L);
        dateData.add(3241640586206673582L);
        dateData.add(3304434974210474223L);
        dateData.add(3274690263425258031L);
        dateData.add(3286257654531981519L);
        dateData.add(3362271912564222481L);
        dateData.add(3372186816159294545L);
        dateData.add(3337484651429058672L);
        dateData.add(2992115508818220552L);
        dateData.add(3002030416708259912L);
        dateData.add(2972285705923043719L);
        dateData.add(3043342514305232297L);
        dateData.add(3054909901116988489L);
        dateData.add(3020207740681719912L);
        dateData.add(3096221998713960874L);
        dateData.add(3106136902309032939L);
        dateData.add(3076392191523816746L);
        dateData.add(3140839062744301515L);
        dateData.add(3163973840662781196L);
        dateData.add(3121009259849124683L);
        dateData.add(2773987634021602434L);
        dateData.add(2792164953700095139L);
        dateData.add(2750852856103122754L);
        dateData.add(2826867114135363716L);
        dateData.add(2836782022025403076L);
        dateData.add(2807037311240186883L);
        dateData.add(2878094119622375461L);
        dateData.add(2888009023217447525L);
        dateData.add(2859916795648915461L);
        dateData.add(2869831699243987525L);
        dateData.add(2945845961571195783L);
        dateData.add(2902881380757539270L);
        dateData.add(2926016150086084358L);
        dateData.add(4266180631522399047L);
        dateData.add(4224868533925426662L);
        dateData.add(4248003307548939047L);
        dateData.add(4310797699847706984L);
        dateData.add(4281052984767523495L);
        dateData.add(4290967892657562856L);
        dateData.add(4366982150689803818L);
        dateData.add(4376897058579843178L);
        dateData.add(4347152343499659689L);
        dateData.add(4418209151881848267L);
        dateData.add(4428124059771887627L);
        dateData.add(4400031827908388266L);

    }

    @Override
    public Set<ICubeFieldSource> getParentFields(Set<CubeTableSource> sources) {
        return new HashSet<ICubeFieldSource>();
    }

    @Override
    public Set<ICubeFieldSource> getFacetFields(Set<CubeTableSource> sources) {
        Set<ICubeFieldSource> result = new HashSet<ICubeFieldSource>();
        for (ICubeFieldSource source : getFieldsArray(sources)) {
            result.add(source);
        }
        return result;
    }

    @Override
    public Set<ICubeFieldSource> getSelfFields(Set<CubeTableSource> sources) {
        Set<ICubeFieldSource> result = new HashSet<ICubeFieldSource>();
        for (ICubeFieldSource source : getFieldsArray(sources)) {
            result.add(source);
        }
        return result;
    }


    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.getTime());

        for (int i = 0; i < rowNumber; i++) {
            System.out.print("stringData.add(\"" + BIRandomUitils.getRandomCharacterString(2) + "\");");
            if ((i + 1) % 5 == 0) {
                System.out.println("");
            }


        }
        for (int i = 0; i < rowNumber; i++) {
            System.out.print("floatData.add(" + BIRandomUitils.getRandomDouble() + ");");
            if ((i + 1) % 5 == 0) {
                System.out.println("");

            }
        }
        for (int i = 0; i < rowNumber; i++) {
            System.out.print("longData.add(" + BIRandomUitils.getRandomLong() + "L);");
            if ((i + 1) % 5 == 0) {
                System.out.println("");
            }
        }

        for (int i = 0; i < rowNumber; i++) {
            System.out.print("doubleData.add(" + BIRandomUitils.getRandomDouble() + ");");
            if ((i + 1) % 5 == 0) {
                System.out.println("");
            }

        }
        for (int i = 0; i < rowNumber; i++) {
            System.out.print("dateData.add(" + BIRandomUitils.getRandomLongAbs() + "L);");
            if ((i + 1) % 5 == 0) {
                System.out.println("");
            }

        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public SourceFile getSourceFile() {
        return null;
    }

    @Override
    public IPersistentTable getPersistentTable() {
        return  new PersistentTable(StringUtils.EMPTY,getTableName(), StringUtils.EMPTY);
    }

    @Override
    public String getSourceID() {
        return "BIMemDataSourceTestTool";
    }

    @Override
    public BICubeFieldSource[] getFieldsArray(Set<CubeTableSource> sources) {
        BICubeFieldSource[] fields = new BICubeFieldSource[5];
        fields[0] = DBFieldTestTool.generateDATE();
        fields[1] = DBFieldTestTool.generateSTRING();
        fields[2] = DBFieldTestTool.generateDOUBLE();
        fields[3] = DBFieldTestTool.generateLONG();
        fields[4] = DBFieldTestTool.generateDOUBLE("double2");
        return fields;
    }

    public List<String> getStringData() {
        return stringData;
    }

    public List<Long> getDateData() {
        return dateData;
    }

    public List<Double> getFloatData() {
        return floatData;
    }

    public List<Long> getLongData() {
        return longData;
    }

    public List<Double> getDoubleData() {
        return doubleData;
    }

    @Override
    public String getTableName() {
        return "MemoryTable";
    }

    @Override
    public Map<Integer, Set<CubeTableSource>> createGenerateTablesMap() {
        return null;
    }

    @Override
    public List<Set<CubeTableSource>> createGenerateTablesList() {
        return null;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public long read(Traversal<BIDataValue> travel, ICubeFieldSource[] field, ICubeDataLoader loader) {
        for (int i = 0; i < getRowNumber(); i++) {
            travel.actionPerformed(new BIDataValue(i, 0, dateData.get(i)));
            travel.actionPerformed(new BIDataValue(i, 1, stringData.get(i)));
            travel.actionPerformed(new BIDataValue(i, 2, floatData.get(i)));
            travel.actionPerformed(new BIDataValue(i, 3, longData.get(i)));
            travel.actionPerformed(new BIDataValue(i, 4, doubleData.get(i)));
        }
        return getRowNumber();
    }

    @Override
    public long read4Part(Traversal<BIDataValue> travel, ICubeFieldSource[] field, ICubeDataLoader loader, int start, int end) {
        return 0;
    }

    @Override
    public Set getFieldDistinctNewestValues(String fieldName, ICubeDataLoader loader, long userId) {
        return null;
    }

    @Override
    public Set getFieldDistinctValuesFromCube(String fieldName, ICubeDataLoader loader, long userId) {
        return null;
    }

    @Override
    public JSONObject createPreviewJSON(ArrayList<String> fields, ICubeDataLoader loader, long userId) throws
            Exception {
        return null;
    }

    @Override
    public TableData createTableData(List<String> fields, ICubeDataLoader loader, long userId) throws Exception {
        return null;
    }

    @Override
    public JSONObject createPreviewJSONFromCube(ArrayList<String> fields, ICubeDataLoader loader) throws Exception {
        return null;
    }

    @Override
    public boolean needGenerateIndex() {
        return false;
    }

    @Override
    public Map<BICore, CubeTableSource> createSourceMap() {
        return null;
    }

    @Override
    public Set<String> getUsedFields(CubeTableSource source) {
        return null;
    }

    @Override
    public void refresh() {

    }

    @Override
    public BICore fetchObjectCore() {
        return null;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        return null;
    }

    @Override
    public void readXML(XMLableReader xmLableReader) {

    }

    @Override
    public void writeXML(XMLPrintWriter xmlPrintWriter) {

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

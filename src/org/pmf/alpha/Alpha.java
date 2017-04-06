package org.pmf.alpha;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XEventImpl;
import org.pmf.alpha.model.EasyEventLog;
import org.pmf.alpha.model.PetriNet;
import org.pmf.alpha.model.Place;
import org.pmf.alpha.model.Trace;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by huangtao on 2016/12/29.
 */
public class Alpha {

    public static int FOOTPRINT_FRONT = 0;
    public static int FOOTPRINT_BEHIND = 1;
    public static int FOOTPRINT_NONE = 2;
    public static int FOOTPRINT_CONCURRENT = 3;

    private File xesFile;

    private XLog log;

    public Alpha(String path) {
        xesFile = new File(path);
    }

    public Alpha(XLog log) {
        this.log = log;
    }

    public PetriNet getPetriNet() {
        if (xesFile != null) {
            log = parseFile(xesFile);
        }
        if (log == null) {
            return null;
        }
        EasyEventLog eventLog = parseXLog(log);

        int[][] footprint = getFootPrint(eventLog);

        PetriNet petriNet = createPetriNet(eventLog, footprint);
        petriNet.showPlace();

        return petriNet;
    }

    private XLog parseFile(File xesFile) {
        XesXmlParser parser = new XesXmlParser();
        XLog log = null;
        if (!parser.canParse(xesFile)) {
            return null;
        }
        try {
            List<XLog> logs = parser.parse(xesFile);
            // 若存在多个日志，只处理第一个
            log = logs.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return log;
    }

    private EasyEventLog parseXLog(XLog log) {
        EasyEventLog easyEventLog = new EasyEventLog();
        try {

            List<XEventClassifier> eventClassifiers = log.getClassifiers();
            // TODO: 2016/12/29 classifier选择器
            XEventClassifier classifier = eventClassifiers.get(1);
            String attribute = classifier.getDefiningAttributeKeys()[0];
            // TODO: 2016/12/29 不确定这个值取的是否绝对正确，有空验证一下
            Iterator<XEventClass> iterator = XLogInfoFactory.createLogInfo(log).getNameClasses().getClasses()
                    .iterator();
            while (iterator.hasNext()) {
                easyEventLog.eventName.add(iterator.next().getId());
            }

            for (int i = 0; i < log.size(); i++) {
                XTrace trace = log.get(i);
                Trace tmpTrace = new Trace();
                for (int j = 0; j < trace.size(); j++) {
                    XEventImpl event = (XEventImpl) trace.get(j);
                    XAttributeMap map = event.getAttributes();
                    tmpTrace.events.add(easyEventLog.eventName.indexOf(map.get(attribute).toString()));
                }

                boolean flag = false;
                for (Trace traceItem : easyEventLog.traces) {
                    if (traceItem.equals(tmpTrace)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    easyEventLog.traces.add(tmpTrace);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return easyEventLog;
    }

    private int[][] getFootPrint(EasyEventLog eventLog) {
        int[][] footprint = new int[eventLog.eventName.size()][eventLog.eventName.size()];
        // 初始化
        for (int i = 0; i < eventLog.eventName.size(); i++) {
            for (int j = 0; j < eventLog.eventName.size(); j++) {
                footprint[i][j] = FOOTPRINT_NONE;
            }
        }

        for (Trace trace : eventLog.traces) {
            for (int i = 0; i < trace.events.size() - 1; i++) {
                int a = trace.events.get(i);
                int b = trace.events.get(i + 1);
                if (footprint[b][a] == FOOTPRINT_FRONT) {
                    footprint[a][b] = FOOTPRINT_CONCURRENT;
                    footprint[b][a] = FOOTPRINT_CONCURRENT;
                } else {
                    footprint[a][b] = FOOTPRINT_FRONT;
                    footprint[b][a] = FOOTPRINT_BEHIND;
                }
            }
        }
        return footprint;
    }

    private PetriNet createPetriNet(EasyEventLog eventLog, int[][] footprint) {
        PetriNet petriNet = new PetriNet(eventLog.eventName);
        ArrayList<Place> tmpPlaces = new ArrayList<>();

        // 找出所有符合要求的库所
        for (int i = 0; i < footprint.length; i++) {
            for (int j = 0; j < footprint[i].length; j++) {
                if (footprint[i][j] == FOOTPRINT_FRONT) {
                    tmpPlaces.add(new Place(i, j, petriNet.transitions));
                    int m = tmpPlaces.size();
                    for (int k = 0; k < m; k++) {
                        Place place = tmpPlaces.get(k);
                        if (place.canMerge(i, j, footprint)) {
                            // 复制这个库所，并把i j加入库所
                            Place newPlace = new Place(petriNet.transitions);
                            newPlace.leftTransition.addAll(place.leftTransition);
                            newPlace.rightTransition.addAll(place.rightTransition);

                            if (!newPlace.leftTransition.contains(i)) {
                                newPlace.leftTransition.add(i);
                            }
                            if (!newPlace.rightTransition.contains(j)) {
                                newPlace.rightTransition.add(j);
                            }
                            tmpPlaces.add(newPlace);
                        }
                    }
                }
            }
        }

        // 删除多余库所，只保留最大
        for (Place tmpPlace : tmpPlaces) {
            boolean shouldInsert = true;
            ArrayList<Place> removePlace = new ArrayList<>();
            for (int i = 0; i < petriNet.places.size(); i++) {
                Place place = petriNet.places.get(i);
                if (tmpPlace.canContain(place)) {
                    removePlace.add(place);
                    // 继续找还有没有能删掉的
                    for (int j = i + 1; j < petriNet.places.size(); j++) {
                        place = petriNet.places.get(j);
                        if (tmpPlace.canContain(place)) {
                            removePlace.add(place);
                        }
                    }
                    break;
                } else if (place.canContain(tmpPlace)) {
                    shouldInsert = false;
                    break;
                }
            }
            if (shouldInsert) {
                petriNet.places.removeAll(removePlace);
                petriNet.addPlace(tmpPlace);
            }
        }
        return petriNet;
    }

}

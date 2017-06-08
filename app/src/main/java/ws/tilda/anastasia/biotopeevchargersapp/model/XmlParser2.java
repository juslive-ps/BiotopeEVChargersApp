package ws.tilda.anastasia.biotopeevchargersapp.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ws.tilda.anastasia.biotopeevchargersapp.model.objects.Address;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.Charger;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.GeoCoordinates;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.OpeningHours;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.ParkingLot;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.ParkingSection;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.ParkingService;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.ParkingSpot;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.Plug;
import ws.tilda.anastasia.biotopeevchargersapp.model.objects.Vehicle;

public class XmlParser2 {
    private ParkingService parkingService = new ParkingService();

    public ParkingService parse(InputStream is) throws XmlPullParserException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList objects = doc.getElementsByTagName("Objects");

            if (objects.getLength() > 0) {
                Node object = objects.item(0);
                if (object.getNodeType() == Node.ELEMENT_NODE) {
                    Element objectElement = (Element) object;
                    parkingService = parseObjectsElement(objectElement);
                }
            }

        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }


        return parkingService;
    }

    public String parseResponseCode(InputStream is) throws XmlPullParserException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        String returnCode = null;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList objects = doc.getElementsByTagName("return");

            if (objects.getLength() > 0) {
                Node object = objects.item(0);
                if (object.getNodeType() == Node.ELEMENT_NODE) {
                    Element objectElement = (Element) object;
                    returnCode = objectElement.getAttribute("returnCode");

                }
            }
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return returnCode;
    }

    private ParkingService parseObjectsElement(Element element) {
        ParkingService pService = new ParkingService();
        NodeList objects = element.getChildNodes();
        for (int i = 0; i < objects.getLength(); i++) {
            Node object = objects.item(i);

            if (object.getNodeType() == Node.ELEMENT_NODE) {
                Element objectElement = (Element) object;
                pService = parseObjectParkingService(objectElement);
            }
        }
        return pService;
    }

    private ParkingService parseObjectParkingService(Element element) {
        ParkingService parkingService = new ParkingService();

        NodeList objects = element.getChildNodes();
        for (int i = 0; i < objects.getLength(); i++) { // id, object(list)
            Node object = objects.item(i);

            if (object.getNodeType() == Node.ELEMENT_NODE) {
                Element objectElement = (Element) object;

                if (objectElement.getTagName().equals("id")) {
                    String id = objectElement.getFirstChild().getNodeValue();
                    parkingService.setId(id);
                } else {
                    List<ParkingLot> parkingLots = parseParkingServiceList(objectElement);
                    parkingService.setParkingLots(parkingLots);
                }
            }
        }
        return parkingService;
    }

    private List<ParkingLot> parseParkingServiceList(Element objectElement) {
        List<ParkingLot> parkingLots = new ArrayList<>();
        NodeList nodes = objectElement.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element nodeElement = (Element) node;

                if (!nodeElement.getTagName().equals("id")) {
                    ParkingLot parkingLot = parseParkingFacility(nodeElement);
                    parkingLots.add(parkingLot);
                }
            }
        }
        return parkingLots;
    }

    private ParkingLot parseParkingFacility(Element nodeElement) {
        ParkingLot parkingLot = new ParkingLot();

        NodeList nodes = nodeElement.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) { // id, object(list)
            Node node = nodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                if (element.getTagName().equals("id")) {
                    String id = element.getFirstChild().getTextContent();
                    parkingLot.setId(id);
                } else if (element.getTagName().equals("InfoItem")) {
                    if (element.getAttribute("name").equals("MaxParkingHours")) {
                        String maxParkingHours = getValue("value", element);
                        parkingLot.setMaxParkingHours(maxParkingHours);
                    } else if (element.getAttribute("name").equals("Owner")) {
                        String owner = getValue("value", element);
                        parkingLot.setOwner(owner);
                    }
                } else if (element.getTagName().equals("Object")) {
                    if (element.getAttribute("type").equals("schema:GeoCoordinates")) {
                        GeoCoordinates position = parseGeoCoordinates(element);
                        parkingLot.setPosition(position);
                    } else if (element.getAttribute("type").equals("schema:OpeningHoursSpecification")) {
                        OpeningHours openingHours = parseOpeningHoursSpecification(element);
                        parkingLot.setOpeningHours(openingHours);
                    } else if (element.getAttribute("type").equals("list")) {
                        parkingLot.setParkingSectionList(parseParkingSectionList(element));
                    }
                }
            }
        }
        return parkingLot;
    }


    private GeoCoordinates parseGeoCoordinates(Element objectElement) {
        GeoCoordinates position = new GeoCoordinates();

        NodeList nodes = objectElement.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element nodeElement = (Element) node;

                if (nodeElement.getTagName().equals("id")) {
                    continue;
                } else if (nodeElement.getTagName().equals("InfoItem")) {
                    if (nodeElement.getAttribute("name").equals("longitude")) {
                        String lon = getValue("value", nodeElement);
                        double longitude = Double.parseDouble(lon);
                        position.setLongitude(longitude);
                    } else if (nodeElement.getAttribute("name").equals("latitude")) {
                        String lat = getValue("value", nodeElement);
                        double latitude = Double.parseDouble(lat);
                        position.setLatitude(latitude);
                    }
                } else if (nodeElement.getTagName().equals("Object")) {
                    if (nodeElement.getAttribute("type").equals("schema:PostalAddress")) {
                        Address address = parseAddress(nodeElement);
                        position.setAddress(address);
                    }
                }
            }
        }
        return position;
    }

    private Address parseAddress(Element objectElement) {
        Address address = new Address();
        NodeList nodes = objectElement.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element nodeElement = (Element) node;

                if (nodeElement.getTagName().equals("id")) {
                    continue;
                } else if (nodeElement.getTagName().equals("InfoItem")) {
                    if (nodeElement.getAttribute("name").equals("addressCountry")) {
                        String country = getValue("value", nodeElement);
                        address.setCountry(country);
                    } else if (nodeElement.getAttribute("name").equals("streetAddress")) {
                        String street = getValue("value", nodeElement);
                        address.setStreet(street);
                    } else if (nodeElement.getAttribute("name").equals("addressRegion")) {
                        String region = getValue("value", nodeElement);
                        address.setRegion(region);
                    } else if (nodeElement.getAttribute("name").equals("addressLocality")) {
                        String locality = getValue("value", nodeElement);
                        address.setLocality(locality);
                    } else if (nodeElement.getAttribute("name").equals("postalCode")) {
                        String postalCode = getValue("value", nodeElement);
                        address.setPostalCode(postalCode);
                    }
                }
            }
        }

        return address;
    }

    private OpeningHours parseOpeningHoursSpecification(Element objectElement) {
        OpeningHours openingHours = new OpeningHours();

        NodeList nodes = objectElement.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element nodeElement = (Element) node;

                if (nodeElement.getTagName().equals("id")) {
                    continue;
                } else if (nodeElement.getTagName().equals("InfoItem")) {
                    if (nodeElement.getAttribute("name").equals("closes")) {
                        String close = getValue("value", nodeElement);
                        openingHours.setClose(close);
                    } else if (nodeElement.getAttribute("name").equals("opens")) {
                        String open = getValue("value", nodeElement);
                        openingHours.setOpen(open);
                    }
                }
            }
        }
        return openingHours;
    }

    private List<ParkingSection> parseParkingSectionList(Element objectElement) {
        List<ParkingSection> parkingSections = new ArrayList<>();
        NodeList nodes = objectElement.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element nodeElement = (Element) node;

                if (!nodeElement.getTagName().equals("id")) {
                    ParkingSection parkingSection = parseParkingSection(nodeElement);
                    parkingSections.add(parkingSection);
                }
            }
        }
        return parkingSections;
    }

    private ParkingSection parseParkingSection(Element nodeElement) {

        ParkingSection parkingSection = new ParkingSection();


        NodeList nodes = nodeElement.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                if (element.getTagName().equals("id")) {
                    String id = element.getFirstChild().getTextContent();
                    parkingSection.setId(id);
                } else if (element.getTagName().equals("InfoItem")) {
                    if (element.getAttribute("name").equals("MaxLength")) {
                        String mLength = getValue("value", element);
                        double maxLength = Double.parseDouble(mLength);
                        parkingSection.setMaxLength(maxLength);
                    } else if (element.getAttribute("name").equals("MaxWidth")) {
                        String mWidth = getValue("value", element);
                        double maxWidth = Double.parseDouble(mWidth);
                        parkingSection.setMaxWidth(maxWidth);
                    } else if (element.getAttribute("name").equals("TotalCapacity")) {
                        String nSpots = getValue("value", element);
                        int numberOfSpots = Integer.valueOf(nSpots);
                        parkingSection.setNumberOfSpots(numberOfSpots);
                    } else if (element.getAttribute("name").equals("HourlyPrice")) {
                        String price = getValue("value", element);
                        double hourlyPrice = Double.parseDouble(price);
                        parkingSection.setHourlyPrice(hourlyPrice);
                    } else if (element.getAttribute("name").equals("NumberOfVacantParkingSpaces")) {
                        String nSpotsAvailable = getValue("value", element);
                        int numberOfSpotsAvailable = Integer.valueOf(nSpotsAvailable);
                        parkingSection.setNumberOfSpotsAvailable(numberOfSpotsAvailable);
                    }
                } else if (element.getTagName().equals("Object")) {
                    if (element.getAttribute("type").equals("list")) {
                        List<ParkingSpot> parkingSpots = parseParkingSpotsList(element);
                        parkingSection.setParkingSpots(parkingSpots);
                    } else if (element.getAttribute("type").equals("schema:Vechile")) {
                        Vehicle vehicle = parseVehicle(element);
                        parkingSection.setVehicle(vehicle);
                    }
                }
            }
        }
        return parkingSection;
    }

    private Vehicle parseVehicle(Element nodeElement) {
        Vehicle vehicle = new Vehicle();

        NodeList nodes = nodeElement.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                if (element.getTagName().equals("id")) {
                    continue;
                } else if (element.getTagName().equals("InfoItem")) {
                    if (element.getAttribute("name").equals("depth")) {
                        String depth = getValue("value", element).toLowerCase();
                        vehicle.setDepth(depth);
                    } else if (element.getTagName().equals("InfoItem")) {
                        if (element.getAttribute("name").equals("height")) {
                            String height = getValue("value", element).toLowerCase();
                            vehicle.setHeight(height);
                        }
                    } else if (element.getTagName().equals("InfoItem")) {
                        if (element.getAttribute("name").equals("width")) {
                            String width = getValue("value", element).toLowerCase();
                            vehicle.setWidth(width);
                        }
                    }
                }
            }
        }

        return vehicle;
    }

    private Charger parseCharger(Element nodeElement) {
        Charger charger = new Charger();

        NodeList nodes = nodeElement.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                if (element.getTagName().equals("id")) {
                    String id = element.getFirstChild().getTextContent();
                    charger.setId(id);
                } else if (element.getTagName().equals("InfoItem")) {
                    if (element.getAttribute("name").equals("Model")) {
                        String model = getValue("value", element).toLowerCase();
                        charger.setModel(model);
                    } else if (element.getAttribute("name").equals("Brand")) {
                        String brand = getValue("value", element);
                        charger.setBrand(brand);
                    }
                } else if (element.getTagName().equals("Object")) {
                    if (element.getAttribute("type").equals("mv:Plug")) {
                        Plug plug = parsePlug(element);
                        charger.setPlug(plug);
                    }
                }
            }
        }
        return charger;
    }

    private Plug parsePlug(Element nodeElement) {
        Plug plug = new Plug();

        NodeList nodes = nodeElement.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                if (element.getTagName().equals("id")) {
                    String id = element.getFirstChild().getTextContent();
                    plug.setId(id);
                } else if (element.getTagName().equals("InfoItem")) {
                    if (element.getAttribute("name").equals("ChargingSpeed")) {
                        String chargingSpeed = getValue("value", element).toLowerCase();
                        plug.setChargingSpeed(chargingSpeed);
                    } else if (element.getAttribute("name").equals("Voltage")) {
                        String voltage = getValue("value", element);
                        plug.setVoltage(voltage);
                    } else if (element.getAttribute("name").equals("Power")) {
                        String power = getValue("value", element);
                        plug.setPower(power);
                    } else if (element.getAttribute("name").equals("PlugType")) {
                        String plugType = getValue("value", element);
                        plug.setPlugType(plugType);
                    } else if (element.getAttribute("name").equals("CableAvailable")) {
                        String cableAvailable = getValue("value", element).toLowerCase();
                        boolean isCableAvailable = Boolean.valueOf(cableAvailable);
                        plug.setCableAvailable(isCableAvailable);
                    } else if (element.getAttribute("name").equals("LockerAvailable")) {
                        String lockerAvailable = getValue("value", element).toLowerCase();
                        boolean isLockerAvailable = Boolean.valueOf(lockerAvailable);
                        plug.setLockerAvailable(isLockerAvailable);
                    }
                }
            }
        }
        return plug;

    }

    private List<ParkingSpot> parseParkingSpotsList(Element objectElement) {
        List<ParkingSpot> parkingSpots = new ArrayList<>();
        NodeList nodes = objectElement.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element nodeElement = (Element) node;

                if (!nodeElement.getTagName().equals("id")) {
                    ParkingSpot parkingSpot = parseParkingSpot(nodeElement);
                    parkingSpots.add(parkingSpot);
                }
            }
        }
        return parkingSpots;
    }

    private ParkingSpot parseParkingSpot(Element nodeElement) {
        ParkingSpot parkingSpot = new ParkingSpot();

        NodeList nodes = nodeElement.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                if (element.getTagName().equals("id")) {
                    String id = element.getFirstChild().getTextContent();
                    parkingSpot.setId(id);
                } else if (element.getTagName().equals("InfoItem")) {
                    if (element.getAttribute("name").equals("Available")) {
                        String available = getValue("value", element).toLowerCase();
                        boolean isAvailable = Boolean.valueOf(available);
                        parkingSpot.setAvailable(isAvailable);
                    } else if (element.getAttribute("name").equals("User")) {
                        String user = getValue("value", element);
                        parkingSpot.setUser(user);
                    }
                } else if (element.getAttribute("type").equals("mv:Charger")) {
                    Charger charger = parseCharger(element);
                    parkingSpot.setCharger(charger);

                }
            }
        }
        return parkingSpot;
    }


    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

}


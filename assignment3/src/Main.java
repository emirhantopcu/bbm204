import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class Airport{
    String name;
    String city;
    ArrayList<Flight> flights = new ArrayList<>();
    ArrayList<Airport> linked_to_this = new ArrayList<>();      //Airport objects as nodes in graph

    public Airport(String name, String city) {
        this.name = name;
        this.city = city;
    }
}

class Flight{
    String ID;
    Airport dept;
    Airport arr;
    String dept_date;                   //Flight objects as edges
    String duration;
    int price;
    Date dept_date_obj;
    Date end_date;
    int dur_h;
    int dur_m;

    public Flight(String ID, String dept_date, String duration, int price) throws ParseException {
        this.ID = ID;
        this.dept_date = dept_date;
        this.duration = duration;
        this.price = price;

        String date_without_day = dept_date.split(" ")[0] + " " + dept_date.split(" ")[1];
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date dateObj = sdf.parse(date_without_day);
        this.dept_date_obj = dateObj;
        Calendar calendar = Calendar.getInstance();         //when a flight object gets created all it's infos get calculated
        calendar.setTime(dateObj);
        calendar.add(Calendar.HOUR, Integer.parseInt(duration.split(":")[0]));
        calendar.add(Calendar.MINUTE, Integer.parseInt(duration.split(":")[1]));
        this.end_date = calendar.getTime();
        this.dur_h = Integer.parseInt(duration.split(":")[0]);
        this.dur_m = Integer.parseInt(duration.split(":")[1]);
    }
}

class FlightPlan{
    Stack<Flight> flightsStack = new Stack<>();
    ArrayList<Flight> flightArrayList = new ArrayList<>();
    int total_cost;
    String total_time;
    Date start_date;                                //this is for restoring flight plans
    Date end_date;                                  //while we are trying to find available paths in our graph
                                                                //possible paths get restored in stacks
    public void addFlight(Flight f){ flightsStack.add(f); }

    public void removeFlight(){ flightsStack.pop(); }

    public Flight peek(){
        return flightsStack.peek();
    }

    public boolean isEmpty(){
        return flightsStack.isEmpty();
    }

    public ArrayList<Flight> getFlightArrayList(){
        if (this.flightArrayList.isEmpty()){
            ArrayList<Flight> flightArrayList = new ArrayList<>();
            int count = flightsStack.size();
            for (int i = 0; i < count; i++) {
                flightArrayList.add(flightsStack.pop());            //these stacks gets converted into arraylist here
            }
            Collections.reverse(flightArrayList);
            this.flightArrayList = flightArrayList;
            calculateTotalCostAndTime();
        }
        return flightArrayList;
    }



    public void calculateTotalCostAndTime(){
        int total_price = 0;
        for (Flight f:
             flightArrayList) {
            total_price += f.price;
        }
        this.total_cost = total_price;
        this.start_date = flightArrayList.get(0).dept_date_obj;
        this.end_date = flightArrayList.get(flightArrayList.size()-1).end_date;
        long diff = end_date.getTime() - start_date.getTime();
        long diff_hours = diff / 3600000;
        long diff_mins = diff % 3600000;
        diff_mins = diff_mins / 60000;                              //after the arraylist gets created this function calculates
        if (diff_hours < 10){                                       //this flight plans' infos
            this.total_time = "0" + diff_hours;
        }else {
            this.total_time = Long.toString(diff_hours);
        }if (diff_mins < 10){
            this.total_time += ":0" + diff_mins;
        }else{
            this.total_time += ":" + diff_mins;
        }
    }

    @Override
    public String toString() {
        ArrayList<String> return_string = new ArrayList<>();
        for (Flight f:
             flightArrayList) {                                                 //this is for displaying flight plans on output.txt
            return_string.add(f.ID + "\t" + f.dept.name + "->" + f.arr.name);
        }
        String return_joined = String.join("||", return_string);
        return return_joined + "\t" + total_time + "/" + total_cost;
    }

    public boolean isQuickerThan(FlightPlan p){
        return this.total_time.compareTo(p.total_time) < 0;             //for listProper command
    }

    public boolean isCheaperThan(FlightPlan p){                     //for listProper command
        return this.total_cost < p.total_cost;
    }
}

class FlightPlanner{
    ArrayList<Airport> airports = new ArrayList<>();
    ArrayList<FlightPlan> flightPlans = new ArrayList<>();
    ArrayList<String> cities = new ArrayList<>();

    public FlightPlanner(String args1, String args2){
        File airport_file = new File(args1);
        try (BufferedReader br = new BufferedReader(new FileReader(airport_file))) {
            String line;
            while ((line = br.readLine()) != null) {
                cities.add(line.split("\t")[0]);                                //create all airport nodes
                for (int i = 1; i < line.split("\t").length; i++) {
                    Airport airport = new Airport(line.split("\t")[i],line.split("\t")[0]);
                    airports.add(airport);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File flight_file = new File(args2);
        try (BufferedReader br = new BufferedReader(new FileReader(flight_file))) {
            String line;
            while ((line = br.readLine()) != null) {                                    //create all flight edge nodes
                String[] line_split = line.split("\t");
                Flight flight = new Flight(line_split[0],line_split[2],line_split[3],Integer.parseInt(line_split[4]));
                String arr_string = line_split[1].split("->")[1];
                for (Airport a:
                        airports) {
                    if (a.name.equals(arr_string)){
                        flight.arr = a;
                    }
                }
                String dept_string = line_split[1].split("->")[0];
                for (Airport a:
                        airports) {
                    if (a.name.equals(dept_string)){
                        flight.dept = a;
                        flight.arr.linked_to_this.add(flight.dept);
                        a.flights.add(flight);
                    }
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<FlightPlan> listAll(String dept_city, String arr_city, String date) throws ParseException {
        ArrayList<String> visited_cities = new ArrayList<>();
        for (Airport a:
             airports) {
            FlightPlan fp = new FlightPlan();
            if (a.city.equals(dept_city)){
                for (Airport b:
                     airports) {
                    if(b.city.equals(arr_city)){                            //taking cities as arguments and calculating paths for every
                        listAllSub(a, b, fp, visited_cities);               //airport in the city separately
                    }
                }
            }
        }

        ArrayList<FlightPlan> returnFlightPlans = this.flightPlans;
        Set<FlightPlan> returnSet = new HashSet<>(returnFlightPlans);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date wanted_date = sdf.parse(date);
        returnFlightPlans.removeIf(fp -> {
            try {
                return sdf.parse(sdf.format(fp.getFlightArrayList().get(0).dept_date_obj)).compareTo(wanted_date) < 0;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return false;
        });
        this.flightPlans = new ArrayList<>();
        returnFlightPlans = new ArrayList<>(returnSet);
        return returnFlightPlans;
    }

    public boolean contains(String city, ArrayList<String> cities){
        for (String search:
             cities) {
            if (search.equals(city)){                                   //for checking if a path is crossing some city for the second time
                return true;
            }
        }
        return false;
    }

    private void listAllSub(Airport dept, Airport arr, FlightPlan fp, ArrayList<String> visited_cities){
        if (dept.flights.size() == 0){
            if (!fp.flightsStack.isEmpty()){
                fp.removeFlight();
                visited_cities.remove(visited_cities.size()-1);
            }
            return;
        }
        for (Flight f:
                dept.flights) {
            if (!fp.isEmpty()){
                if((fp.peek().end_date.compareTo(f.dept_date_obj)) < 0  && !contains(f.dept.city,visited_cities)){
                    if (f.arr == arr){
                        fp.addFlight(f);
                        visited_cities.add(f.dept.name);                                    //applying dfs to find all the paths to the wanted airport
                        FlightPlan new_plan = new FlightPlan();                             //updates flightplan arraylist if finds a path
                        new_plan.flightsStack = (Stack<Flight>) fp.flightsStack.clone();    //then continues to find other paths
                        flightPlans.add(new_plan);
                        fp.removeFlight();
                        visited_cities.remove(visited_cities.size()-1);
                    }else{
                        fp.addFlight(f);
                        visited_cities.add(f.dept.name);
                        listAllSub(f.arr, arr, fp , visited_cities);
                        if (!fp.flightsStack.isEmpty()){
                            fp.removeFlight();
                            visited_cities.remove(visited_cities.size()-1);
                        }
                    }
                }
            }else{
                fp.addFlight(f);
                visited_cities.add(f.dept.name);
                listAllSub(f.arr, arr, fp , visited_cities);
                if (!fp.flightsStack.isEmpty()){
                    fp.removeFlight();
                    visited_cities.remove(visited_cities.size()-1);
                }
            }
        }
    }

    public ArrayList<FlightPlan> listProper(String dept_city, String arr_city, String date) throws ParseException {
        ArrayList<FlightPlan> all_plans = listAll(dept_city, arr_city, date);
        if (all_plans.isEmpty()){
            return new ArrayList<>();
        }
        ArrayList<FlightPlan> properPlans = new ArrayList<>(all_plans);
        for (FlightPlan p:                                                              //checking all flight plans to find proper plans
             all_plans) {
            for (FlightPlan l:
                 all_plans) {
                if (p.isCheaperThan(l) && p.isQuickerThan(l)){
                    properPlans.remove(l);
                }
            }
        }
        return properPlans;
    }

    public int diameterOfGraph() throws ParseException {
        ArrayList<Integer> shortest_paths_costs = new ArrayList<>();
        for (String city1:
             cities) {
            for (String city2:
                 cities) {
                ArrayList<FlightPlan> all_plans = listAll(city1, city2, "00/00/0000");
                ArrayList<Integer> costs = new ArrayList<>();
                if (!all_plans.isEmpty()){
                    for (FlightPlan p:                                              //finding all paths between every possible vertex and restoring their
                            all_plans) {                                            //costs in an arraylist, thus it finds graph diameter
                        costs.add(p.total_cost);
                    }
                    shortest_paths_costs.add(Collections.min(costs));
                }
            }
        }
        return Collections.max(shortest_paths_costs);
    }

    public double pageRankOfNodes(Airport airport){
        int sum = 0;
        for (Airport link:                                                  //i couldn't handle this so i tried to implement you gave on the pdf
             airport.linked_to_this) {
            sum += pageRankOfNodes(link) + link.flights.size();
        }
        return ((1 - 0.85)/airports.size() + (0.85 * sum));
    }
}

public class Main {

    public static void main(String[] args) throws ParseException, FileNotFoundException {
        PrintStream outputFile = new PrintStream(new FileOutputStream("output.txt"));
        System.setOut(outputFile);
        File flight_file = new File(args[2]);
        try (BufferedReader br = new BufferedReader(new FileReader(flight_file))) {
            String line;
            while ((line = br.readLine()) != null) {                                //reading output lines and executing commands
                String[] line_split = line.split("\t");
                String command = line_split[0];
                FlightPlanner fp = new FlightPlanner(args[0],args[1]);
                if (command.equals("listAll")){
                    System.out.println("command : " + line);
                    String dept = line_split[1].split("->")[0];
                    String arr = line_split[1].split("->")[1];
                    String date = line_split[2];
                    ArrayList<FlightPlan> all_plans = fp.listAll(dept, arr, date);
                    if (all_plans.isEmpty()){
                        System.out.println("No suitable flight plan is found");
                        System.out.println();
                        System.out.println();
                        continue;
                    }                                                        //sorry teacher i don't have enough time to complete commenting :(
                    for (FlightPlan p:
                            all_plans) {
                        System.out.println(p.toString());
                    }
                    System.out.println();
                    System.out.println();
                }else if(command.equals("listProper")){
                    System.out.println("command : " + line);
                    String dept = line_split[1].split("->")[0];
                    String arr = line_split[1].split("->")[1];
                    String date = line_split[2];
                    ArrayList<FlightPlan> properPlans = fp.listProper(dept, arr, date);
                    if (properPlans.isEmpty()){
                        System.out.println("No suitable flight plan is found");
                        System.out.println();
                        System.out.println();
                        continue;
                    }
                    for (FlightPlan p:
                         properPlans) {
                        System.out.println(p.toString());
                    }
                    System.out.println();
                    System.out.println();
                }else if(command.equals("listCheapest")){
                    System.out.println("command : " + line);
                    String dept = line_split[1].split("->")[0];
                    String arr = line_split[1].split("->")[1];
                    String date = line_split[2];
                    ArrayList<FlightPlan> all_plans = fp.listAll(dept, arr, date);
                    if (all_plans.isEmpty()){
                        System.out.println("No suitable flight plan is found");
                        System.out.println();
                        System.out.println();
                        continue;
                    }
                    ArrayList<FlightPlan> min_cost_plans = new ArrayList<>();
                    FlightPlan min_cost_plan = Collections.min(all_plans, new Comparator<FlightPlan>() {
                        @Override
                        public int compare(FlightPlan o1, FlightPlan o2) {
                            return Integer.compare(o1.total_cost, o2.total_cost);
                        }
                    });
                    min_cost_plans.add(min_cost_plan);
                    all_plans.remove(min_cost_plan);
                    for (FlightPlan p:
                            all_plans) {
                        if (p.total_cost == min_cost_plan.total_cost){
                            min_cost_plans.add(p);
                        }
                    }
                    for (FlightPlan p:
                         min_cost_plans) {
                        System.out.println(p.toString());
                    }
                    System.out.println();
                    System.out.println();
                }else if(command.equals("listQuickest")){
                    System.out.println("command : " + line);
                    String dept = line_split[1].split("->")[0];
                    String arr = line_split[1].split("->")[1];
                    String date = line_split[2];
                    ArrayList<FlightPlan> all_plans = fp.listAll(dept, arr, date);
                    if (all_plans.isEmpty()){
                        System.out.println("No suitable flight plan is found");
                        System.out.println();
                        System.out.println();
                        continue;
                    }
                    ArrayList<FlightPlan> min_time_plans = new ArrayList<>();
                    FlightPlan min_time_plan = Collections.min(all_plans, new Comparator<FlightPlan>() {
                        @Override
                        public int compare(FlightPlan o1, FlightPlan o2) {
                            return o1.total_time.compareTo(o2.total_time);
                        }
                    });
                    min_time_plans.add(min_time_plan);
                    all_plans.remove(min_time_plan);
                    for (FlightPlan p:
                            all_plans) {
                        if (p.total_time == min_time_plan.total_time){
                            min_time_plans.add(p);
                        }
                    }
                    for (FlightPlan p:
                            min_time_plans) {
                        System.out.println(p.toString());
                    }
                    System.out.println();
                    System.out.println();
                }else if(command.equals("listCheaper")){
                    System.out.println("command : " + line);
                    String dept = line_split[1].split("->")[0];
                    String arr = line_split[1].split("->")[1];
                    String date = line_split[2];
                    ArrayList<FlightPlan> properPlans = fp.listProper(dept, arr, date);
                    ArrayList<FlightPlan> cheaper_than = new ArrayList<>();
                    for (FlightPlan p:
                         properPlans) {
                        if (p.total_cost < Integer.parseInt(line_split[3])){
                            cheaper_than.add(p);
                        }
                    }
                    if (cheaper_than.isEmpty()){
                        System.out.println("No suitable flight plan is found");
                    }else {
                        for (FlightPlan p:
                                cheaper_than) {
                            System.out.println(p.toString());
                        }
                    }
                    System.out.println();
                    System.out.println();
                }else if(command.equals("listQuicker")){
                    System.out.println("command : " + line);
                    String dept = line_split[1].split("->")[0];
                    String arr = line_split[1].split("->")[1];
                    String date = line_split[2];
                    ArrayList<FlightPlan> properPlans = fp.listProper(dept, arr, date);
                    ArrayList<FlightPlan> quicker_than = new ArrayList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    Date wanted_date = sdf.parse(line_split[3]);
                    for (FlightPlan p:
                            properPlans) {
                        if (p.end_date.compareTo(wanted_date) < 0){
                            quicker_than.add(p);
                        }
                    }
                    if (quicker_than.isEmpty()){
                        System.out.println("No suitable flight plan is found");
                    }else {
                        for (FlightPlan p:
                                quicker_than) {
                            System.out.println(p.toString());
                        }
                    }
                    System.out.println();
                    System.out.println();
                }else if(command.equals("listExcluding")){
                    System.out.println("command : " + line);
                    String dept = line_split[1].split("->")[0];
                    String arr = line_split[1].split("->")[1];
                    String date = line_split[2];
                    ArrayList<FlightPlan> properPlans = fp.listProper(dept, arr, date);
                    ArrayList<FlightPlan> properPlansExcluding = new ArrayList<>();
                    for (FlightPlan p:
                         properPlans) {
                        for (Flight f:
                             p.getFlightArrayList()) {
                            if (f.ID.substring(0,2).equals(line_split[3])){
                                properPlansExcluding.add(p);
                            }
                        }
                    }
                    for (FlightPlan p:
                         properPlansExcluding) {
                        properPlans.remove(p);
                    }
                    if (properPlans.isEmpty()){
                        System.out.println("No suitable flight plan is found");
                    }else{
                        for (FlightPlan p:
                             properPlans) {
                            System.out.println(p.toString());
                        }
                    }
                    System.out.println();
                    System.out.println();
                }else if(command.equals("listOnlyFrom")){
                    System.out.println("command : " + line);
                    String dept = line_split[1].split("->")[0];
                    String arr = line_split[1].split("->")[1];
                    String date = line_split[2];
                    ArrayList<FlightPlan> properPlans = fp.listProper(dept, arr, date);
                    ArrayList<FlightPlan> properPlansOnlyFrom = new ArrayList<>();
                    for (FlightPlan p:
                         properPlans) {
                        int count = 0;
                        for (Flight f:
                                p.getFlightArrayList()) {
                            if (f.ID.substring(0,2).equals(line_split[3])){
                                count++;
                            }
                        }
                        if (count == p.getFlightArrayList().size()){
                            properPlansOnlyFrom.add(p);
                        }
                    }
                    if (properPlansOnlyFrom.isEmpty()){
                        System.out.println("No suitable flight plan is found");
                    }else{
                        for (FlightPlan p:
                                properPlansOnlyFrom) {
                            System.out.println(p.toString());
                        }
                    }
                    System.out.println();
                    System.out.println();
                }else if(command.equals("diameterOfGraph")){
                    System.out.println("command : " + line);
                    System.out.println("the diameter of graph : " + fp.diameterOfGraph());
                    System.out.println();
                    System.out.println();
                }else if(command.equals("pageRankOfNodes")){
                    System.out.println("command : " + line);
                    for (Airport a:
                            fp.airports) {
                        System.out.println(a.name + " : " + fp.pageRankOfNodes(a));
                    }


                    System.out.println();
                    System.out.println();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

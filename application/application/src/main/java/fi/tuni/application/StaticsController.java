package fi.tuni.application;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import fi.tuni.application.classes.DailyForecastData;
import fi.tuni.application.classes.HourlyForecastData;
import fi.tuni.application.classes.HourlyHistoryData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class StaticsController extends BaseSceneController {
    @FXML
    private BorderPane root;
    @FXML
    private RadioButton forecastRadioButton;
    @FXML
    private RadioButton historyRadioButton;
    @FXML
    private RadioButton weekRadioButton;
    @FXML
    private RadioButton todayRadioButton;
    @FXML
    private ChoiceBox<String> chartChoiceBox;
    @FXML
    private ChoiceBox<String> data1ChoiceBox;
    @FXML
    private ChoiceBox<String> data2ChoiceBox;
    @FXML
    private TableView<TableItems> dataTable;
    @FXML
    private TableColumn<TableItems, String> dayColumn;
    @FXML
    private TableColumn<TableItems, Number> data1Column;
    @FXML
    private TableColumn<TableItems, Number> data2Column;
    @FXML
    private StackPane chartPane;

    private ObservableList<TableItems> data = FXCollections.observableArrayList();
    private ObservableList<String> chartOptions = FXCollections.observableArrayList(
            Arrays.stream(ChartChoice.values()).map(ChartChoice::getValue).collect(Collectors.toList()));

    private ObservableList<String> chartDataOptions = FXCollections.observableArrayList(
            Arrays.stream(DataChoice.values()).map(DataChoice::getValue).collect(Collectors.toList()));

    public static class TableItems {
        String day;
        Number data1;
        Number data2;
        static List<Number> data1List = new ArrayList<>();
        static List<Number> data2List = new ArrayList<>();
        static List<String> daysAndHours = new ArrayList<>();

        public TableItems(String day, Number data1, Number data2) {
            this.day = day;
            this.data1 = data1;
            this.data2 = data2;
        }

        public String getDay() {
            return day;
        }

        public Number getData1() {
            return data1;
        }

        public Number getData2() {
            return data2;
        }
    }

    private enum DataChoice {
        TEMPERATURE("Temperature"),
        WIND_SPEED("Wind Speed"),
        PROBABILITY_OF_PRECIPITATION("Probability of precipitation"),
        CLOUDINESS("Cloudiness, %");

        private final String value;

        DataChoice(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private enum ChartChoice {
        LINE_CHART("Line Chart"),
        BAR_CHART("Bar Chart"),
        AREA_CHART("Area Chart");

        private final String value;

        ChartChoice(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private void addDataToTable() {
        for (int i = 0; i < TableItems.daysAndHours.size(); i++) {
            String day = TableItems.daysAndHours.get(i).substring(0, 5);
            Number data1 = TableItems.data1List.get(i);
            Number data2 = TableItems.data2List.get(i);
            if (day != null && data1 != null && data2 != null) {
                data.add(new TableItems(day, data1, data2));
            }
        }
    }

    private String unixTimeToDay(long day) {
        Date date = new Date(day * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.M.y");
        return sdf.format(date);
    }

    private String unixTimeToHour(long hour) {
        Date time = new Date(hour * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd.MM.y");
        return sdf.format(time);
    }

    private XYChart.Series<String, Number> gatherData1() {
        XYChart.Series<String, Number> data1ForecastSeries = new XYChart.Series<String, Number>();
        dataTable.getItems().clear();
        TableItems.data1List.clear();
        TableItems.daysAndHours.clear();
        if (forecastRadioButton.isSelected() && weekRadioButton.isSelected()) {
            for (DailyForecastData.DailyWeatherInfo day : this.dailyForecastData.getList()) {
                TableItems.daysAndHours.add(unixTimeToDay(day.getDt()));
                if (data1ChoiceBox.getValue().equals(DataChoice.TEMPERATURE.getValue())) {
                    data1ForecastSeries.getData()
                            .add(new XYChart.Data<>(unixTimeToDay(day.getDt()), day.getTemp().getDay()));
                    TableItems.data1List.add(day.getTemp().getDay());
                } else if (data1ChoiceBox.getValue().equals(DataChoice.WIND_SPEED.getValue())) {
                    data1ForecastSeries.getData().add(new XYChart.Data<>(unixTimeToDay(day.getDt()), day.getSpeed()));
                    TableItems.data1List.add(day.getSpeed());
                } else if (data1ChoiceBox.getValue().equals(DataChoice.PROBABILITY_OF_PRECIPITATION.getValue())) {
                    data1ForecastSeries.getData().add(new XYChart.Data<>(unixTimeToDay(day.getDt()), day.getPop()));
                    TableItems.data1List.add(day.getPop());
                } else if (data1ChoiceBox.getValue().equals(DataChoice.CLOUDINESS.getValue())) {
                    data1ForecastSeries.getData().add(new XYChart.Data<>(unixTimeToDay(day.getDt()), day.getClouds()));
                    TableItems.data1List.add(day.getClouds());
                }
            }
        } else if (forecastRadioButton.isSelected() && todayRadioButton.isSelected()) {
            for (HourlyForecastData.HourlyWeatherInfo hour : this.hourlyForecastData.getList().subList(0,
                    Math.min(24, this.hourlyForecastData.getList().size()))) {
                TableItems.daysAndHours.add(unixTimeToHour(hour.getDt()));
                if (data1ChoiceBox.getValue().equals(DataChoice.TEMPERATURE.getValue())) {
                    data1ForecastSeries.getData()
                            .add(new XYChart.Data<>(unixTimeToHour(hour.getDt()), hour.getMain().getTemp()));
                    TableItems.data1List.add(hour.getMain().getTemp());
                } else if (data1ChoiceBox.getValue().equals(DataChoice.WIND_SPEED.getValue())) {
                    data1ForecastSeries.getData()
                            .add(new XYChart.Data<>(unixTimeToHour(hour.getDt()), hour.getWind().getSpeed()));
                    TableItems.data1List.add(hour.getWind().getSpeed());
                } else if (data1ChoiceBox.getValue().equals(DataChoice.PROBABILITY_OF_PRECIPITATION.getValue())) {
                    data1ForecastSeries.getData().add(new XYChart.Data<>(unixTimeToHour(hour.getDt()), hour.getPop()));
                    TableItems.data1List.add(hour.getPop());
                } else if (data1ChoiceBox.getValue().equals(DataChoice.CLOUDINESS.getValue())) {
                    data1ForecastSeries.getData()
                            .add(new XYChart.Data<>(unixTimeToHour(hour.getDt()), hour.getClouds().getAll()));
                    TableItems.data1List.add(hour.getClouds().getAll());
                }
            }
        }
        // else if (historyRadioButton.isSelected() && weekRadioButton.isSelected())
        // {
        // for(DailyHistoryData.DailyWeatherInfo day : this.dailyHistoryData.getList())
        // {
        // TableItems.daysAndHours.add(unixTimeToDay(day.getDt()));
        // if(data1ChoiceBox.getValue().equals("Temperature"))
        // {
        // data1ForecastSeries.getData().add(new
        // XYChart.Data<>(unixTimeToDay(day.getDt()),day.getTemp().getDay()));
        // TableItems.data1List.add(day.getTemp().getDay());
        // }
        // else if(data1ChoiceBox.getValue().equals("Wind Speed"))
        // {
        // data1ForecastSeries.getData().add(new
        // XYChart.Data<>(unixTimeToDay(day.getDt()),day.getSpeed()));
        // TableItems.data1List.add(day.getSpeed());
        // }
        // else if(data1ChoiceBox.getValue().equals("Probability of precipitation"))
        // {
        // data1ForecastSeries.getData().add(new
        // XYChart.Data<>(unixTimeToDay(day.getDt()),day.getPop()));
        // TableItems.data1List.add(day.getPop());
        // }
        // else if(data1ChoiceBox.getValue().equals("Cloudiness, %"))
        // {
        // data1ForecastSeries.getData().add(new
        // XYChart.Data<>(unixTimeToDay(day.getDt()),day.getClouds()));
        // TableItems.data1List.add(day.getClouds());
        // }
        // }
        // }
        else if (historyRadioButton.isSelected() && todayRadioButton.isSelected()) {
            for (HourlyHistoryData.HourlyWeatherInfo hour : this.hourlyHistoryData.getList()) {
                TableItems.daysAndHours.add(unixTimeToHour(hour.getDt()));
                if (data1ChoiceBox.getValue().equals("Temperature")) {
                    data1ForecastSeries.getData()
                            .add(new XYChart.Data<>(unixTimeToHour(hour.getDt()), hour.getMain().getTemp()));
                    TableItems.data1List.add(hour.getMain().getTemp());
                } else if (data1ChoiceBox.getValue().equals("Wind Speed")) {
                    data1ForecastSeries.getData()
                            .add(new XYChart.Data<>(unixTimeToHour(hour.getDt()), hour.getWind().getSpeed()));
                    TableItems.data1List.add(hour.getWind().getSpeed());
                } else if (data1ChoiceBox.getValue().equals("Probability of precipitation")) {
                    data1ForecastSeries.getData().add(new XYChart.Data<>(unixTimeToHour(hour.getDt()), hour.getPop()));
                    TableItems.data1List.add(hour.getPop());
                } else if (data1ChoiceBox.getValue().equals("Cloudiness, %")) {
                    data1ForecastSeries.getData()
                            .add(new XYChart.Data<>(unixTimeToHour(hour.getDt()), hour.getClouds().getAll()));
                    TableItems.data1List.add(hour.getClouds().getAll());
                }
            }
        }
        return data1ForecastSeries;
    }

    private XYChart.Series<String, Number> gatherData2() {
        XYChart.Series<String, Number> data2ForecastSeries = new XYChart.Series<String, Number>();
        TableItems.data2List.clear();
        if (forecastRadioButton.isSelected() && weekRadioButton.isSelected()) {
            for (DailyForecastData.DailyWeatherInfo day : this.dailyForecastData.getList()) {
                if (data2ChoiceBox.getValue().equals(DataChoice.TEMPERATURE.getValue())) {
                    data2ForecastSeries.getData()
                            .add(new XYChart.Data<>(unixTimeToDay(day.getDt()), day.getTemp().getDay()));
                    TableItems.data2List.add(day.getTemp().getDay());
                } else if (data2ChoiceBox.getValue().equals(DataChoice.WIND_SPEED.getValue())) {
                    data2ForecastSeries.getData().add(new XYChart.Data<>(unixTimeToDay(day.getDt()), day.getSpeed()));
                    TableItems.data2List.add(day.getSpeed());
                } else if (data2ChoiceBox.getValue().equals(DataChoice.PROBABILITY_OF_PRECIPITATION.getValue())) {
                    data2ForecastSeries.getData().add(new XYChart.Data<>(unixTimeToDay(day.getDt()), day.getPop()));
                    TableItems.data2List.add(day.getPop());
                } else if (data2ChoiceBox.getValue().equals(DataChoice.CLOUDINESS.getValue())) {
                    data2ForecastSeries.getData().add(new XYChart.Data<>(unixTimeToDay(day.getDt()), day.getClouds()));
                    TableItems.data2List.add(day.getClouds());
                }
            }
        } else if (forecastRadioButton.isSelected() && todayRadioButton.isSelected()) {
            for (HourlyForecastData.HourlyWeatherInfo hour : this.hourlyForecastData.getList().subList(0,
                    Math.min(24, this.hourlyForecastData.getList().size()))) {
                if (data2ChoiceBox.getValue().equals(DataChoice.TEMPERATURE.getValue())) {
                    data2ForecastSeries.getData()
                            .add(new XYChart.Data<>(unixTimeToHour(hour.getDt()), hour.getMain().getTemp()));
                    TableItems.data2List.add(hour.getMain().getTemp());
                } else if (data2ChoiceBox.getValue().equals(DataChoice.WIND_SPEED.getValue())) {
                    data2ForecastSeries.getData()
                            .add(new XYChart.Data<>(unixTimeToHour(hour.getDt()), hour.getWind().getSpeed()));
                    TableItems.data2List.add(hour.getWind().getSpeed());
                } else if (data2ChoiceBox.getValue().equals(DataChoice.PROBABILITY_OF_PRECIPITATION.getValue())) {
                    data2ForecastSeries.getData().add(new XYChart.Data<>(unixTimeToHour(hour.getDt()), hour.getPop()));
                    TableItems.data2List.add(hour.getPop());
                } else if (data2ChoiceBox.getValue().equals(DataChoice.CLOUDINESS.getValue())) {
                    data2ForecastSeries.getData()
                            .add(new XYChart.Data<>(unixTimeToHour(hour.getDt()), hour.getClouds().getAll()));
                    TableItems.data2List.add(hour.getClouds().getAll());
                }
            }
        }
        // else if (historyRadioButton.isSelected() && weekRadioButton.isSelected())
        // {
        // for(DailyHistoryData.DailyWeatherInfo day : this.dailyHistoryData.getList())
        // {
        // if(data2ChoiceBox.getValue().equals("Temperature"))
        // {
        // data2ForecastSeries.getData().add(new
        // XYChart.Data<>(unixTimeToDay(day.getDt()),day.getTemp().getDay()));
        // TableItems.data2List.add(day.getTemp().getDay());
        // }
        // else if(data2ChoiceBox.getValue().equals("Wind Speed"))
        // {
        // data2ForecastSeries.getData().add(new
        // XYChart.Data<>(unixTimeToDay(day.getDt()),day.getSpeed()));
        // TableItems.data2List.add(day.getSpeed());
        // }
        // else if(data2ChoiceBox.getValue().equals("Probability of precipitation"))
        // {
        // data2ForecastSeries.getData().add(new
        // XYChart.Data<>(unixTimeToDay(day.getDt()),day.getPop()));
        // TableItems.data2List.add(day.getPop());
        // }
        // else if(data2ChoiceBox.getValue().equals("Cloudiness, %"))
        // {
        // data2ForecastSeries.getData().add(new
        // XYChart.Data<>(unixTimeToDay(day.getDt()),day.getClouds()));
        // TableItems.data2List.add(day.getClouds());
        // }
        // }
        // }
        else if (historyRadioButton.isSelected() && todayRadioButton.isSelected()) {
            for (HourlyHistoryData.HourlyWeatherInfo hour : this.hourlyHistoryData.getList().subList(0,
                    Math.min(24, this.hourlyForecastData.getList().size()))) {
                if (data2ChoiceBox.getValue().equals(DataChoice.TEMPERATURE.getValue())) {
                    data2ForecastSeries.getData()
                            .add(new XYChart.Data<>(unixTimeToHour(hour.getDt()), hour.getMain().getTemp()));
                    TableItems.data2List.add(hour.getMain().getTemp());
                } else if (data2ChoiceBox.getValue().equals(DataChoice.WIND_SPEED.getValue())) {
                    data2ForecastSeries.getData()
                            .add(new XYChart.Data<>(unixTimeToHour(hour.getDt()), hour.getWind().getSpeed()));
                    TableItems.data2List.add(hour.getWind().getSpeed());
                } else if (data2ChoiceBox.getValue().equals(DataChoice.PROBABILITY_OF_PRECIPITATION.getValue())) {
                    data2ForecastSeries.getData().add(new XYChart.Data<>(unixTimeToHour(hour.getDt()), hour.getPop()));
                    TableItems.data2List.add(hour.getPop());
                } else if (data2ChoiceBox.getValue().equals(DataChoice.CLOUDINESS.getValue())) {
                    data2ForecastSeries.getData()
                            .add(new XYChart.Data<>(unixTimeToHour(hour.getDt()), hour.getClouds().getAll()));
                    TableItems.data2List.add(hour.getClouds().getAll());
                }
            }
        }
        return data2ForecastSeries;
    }

    private void drawChart(String chartType) {

        XYChart<String, Number> chart;

        if (chartType.equals(ChartChoice.LINE_CHART.getValue())) {
            chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        } else if (chartType.equals(ChartChoice.BAR_CHART.getValue())) {
            chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        } else if (chartType.equals(ChartChoice.AREA_CHART.getValue())) {
            chart = new AreaChart<>(new CategoryAxis(), new NumberAxis());
        } else {
            throw new IllegalArgumentException("Invalid chart type");
        }

        chart.getData().add(gatherData1());
        chart.getData().add(gatherData2());
        updateTable();
        // Add the newly created chart to the root pane
        chartPane.getChildren().clear();
        chartPane.getChildren().add(chart);
    }

    @FXML
    public void initialize() {
        super.initialize();

        super.addPropertyChangeListener(evt -> {
            if ("currentCity".equals(evt.getPropertyName())) {
                this.drawChart(ChartChoice.LINE_CHART.getValue());
            }
        });
        ToggleGroup forecastHistoryGroup = new ToggleGroup();
        forecastRadioButton.setToggleGroup(forecastHistoryGroup);
        historyRadioButton.setToggleGroup(forecastHistoryGroup);
        forecastRadioButton.setSelected(true);

        forecastRadioButton.setOnAction(e -> {
            this.drawChart(chartChoiceBox.getValue());
        });
        historyRadioButton.setOnAction(e -> {
            if (weekRadioButton.isSelected()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Weekly history data is currently unavailable. Sorry for the inconvenience!");

                alert.showAndWait();
                forecastRadioButton.setSelected(true);
            } else {
                this.drawChart(chartChoiceBox.getValue());
            }
        });
        ToggleGroup intervalGroup = new ToggleGroup();
        weekRadioButton.setToggleGroup(intervalGroup);
        todayRadioButton.setToggleGroup(intervalGroup);
        todayRadioButton.setSelected(true);

        chartChoiceBox.setItems(chartOptions);
        chartChoiceBox.setValue(ChartChoice.LINE_CHART.getValue());
        data1ChoiceBox.setItems(chartDataOptions);
        data1ChoiceBox.setValue(DataChoice.TEMPERATURE.getValue());
        data2ChoiceBox.setItems(chartDataOptions);
        data2ChoiceBox.setValue(DataChoice.WIND_SPEED.getValue());
        data2ChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.equals(data1ChoiceBox.getValue())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Selection");
                alert.setHeaderText(null);
                alert.setContentText("Cannot select the same data type for both choices.");
                alert.showAndWait();

                // Reset the value
                data2ChoiceBox.setValue(oldVal);
            } else {
                this.drawChart(chartChoiceBox.getValue());
            }
        });

        data1ChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.equals(data2ChoiceBox.getValue())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Selection");
                alert.setHeaderText(null);
                alert.setContentText("Cannot select the same data type for both choices.");
                alert.showAndWait();

                data1ChoiceBox.setValue(oldVal);
            } else {
                this.drawChart(chartChoiceBox.getValue());
            }
        });

        weekRadioButton.setOnAction(e -> {
            if (historyRadioButton.isSelected()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
                alert.setHeaderText(null);
                alert.setContentText("Weekly history data is currently unavailable. Sorry for the inconvenience!");

                alert.showAndWait();
                todayRadioButton.setSelected(true);
            } else {
                this.drawChart(chartChoiceBox.getValue());
            }
        });
        todayRadioButton.setOnAction(e -> {
            this.drawChart(chartChoiceBox.getValue());
        });

        chartChoiceBox.setOnAction(e -> {
            if (chartChoiceBox.getValue().equals(ChartChoice.LINE_CHART.getValue())) {
                drawChart(ChartChoice.LINE_CHART.getValue());
            } else if (chartChoiceBox.getValue().equals(ChartChoice.BAR_CHART.getValue())) {
                drawChart(ChartChoice.BAR_CHART.getValue());
            } else if (chartChoiceBox.getValue().equals(ChartChoice.AREA_CHART.getValue())) {
                drawChart(ChartChoice.AREA_CHART.getValue());
            }
        });

        drawChart(ChartChoice.LINE_CHART.getValue());

    }

    private void updateTable() {
        dayColumn.setCellValueFactory(new PropertyValueFactory<TableItems, String>("day"));
        data1Column.setCellValueFactory(new PropertyValueFactory<TableItems, Number>("data1"));
        data2Column.setCellValueFactory(new PropertyValueFactory<TableItems, Number>("data2"));
        data1Column.setText(data1ChoiceBox.getValue().toString());
        data2Column.setText(data2ChoiceBox.getValue().toString());
        addDataToTable();
        dataTable.setItems(data);
    }
}
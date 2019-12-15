package net.pictulog.ml.rummikub.ui;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import net.pictulog.ml.rummikub.model.Player;
import net.pictulog.ml.rummikub.model.Tile;
import net.pictulog.ml.rummikub.service.PlayerController;
import net.pictulog.ml.rummikub.service.PoolController;
import net.pictulog.ml.rummikub.ui.components.UiTile;
import net.pictulog.ml.rummikub.ui.view.HelpView;
import net.pictulog.ml.rummikub.ui.view.PlaygroundView;
import net.pictulog.ml.rummikub.ui.view.SettingsView;

@Component
@Theme("custom")
@PushStateNavigation
@SpringUI(path = "/rummikub")
@JavaScript("https://code.jquery.com/jquery-3.4.1.slim.min.js")
@JavaScript("https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js")
@JavaScript("https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js")
@StyleSheet("https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css")
public class RummikubUI extends UI {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(RummikubUI.class);

    @Value("${rummikubDescription}")
    private String rummikubDescription;

    @Autowired
    private PlayerController playerController;
    
    @Autowired
    private PoolController poolController;

    public RummikubUI() {
    	
    }

    @Override
    protected void init(VaadinRequest request) {
    	LOG.info("Initilializing RummiKub UI");
        // Label title = new Label("Menu");
        // title.addStyleName(ValoTheme.MENU_TITLE);

        Button button1 = new Button("Game",
                e -> getNavigator().navigateTo(getViewName(PlaygroundView.class)));
        Button button2 = new Button("Settings",
                e -> getNavigator().navigateTo(getViewName(SettingsView.class)));
        Button button3 = new Button("Help",
                e -> getNavigator().navigateTo(getViewName(HelpView.class)));
        Button button4 = new Button("Play 1 round!",
                e -> playerController.play(1));
        ComboBox<Player> comboBox = new ComboBox<>("View: ");
        comboBox.setItemCaptionGenerator(new ItemCaptionGenerator<Player>() {
			private static final long serialVersionUID = 1L;
			@Override
			public String apply(Player item) {
				return item.getName();
			}
		});
        comboBox.setItems(playerController.getPlayers());
        comboBox.addValueChangeListener(event -> {
            if (event.getSource().isEmpty()) {
                LOG.info("No player selected");
            } else {
            	LOG.info("Selected player: " + event.getValue());
            }
        });

        
        button1.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);
        button2.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);
        button3.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);

        CssLayout menu = new CssLayout(button1, button2, button3, button4, comboBox);
        menu.addStyleName(ValoTheme.MENU_ROOT);

        CssLayout viewContainer = new CssLayout();
        viewContainer.setSizeFull();

        // XXX: Should use a split layout when available
        // SplitLayout layout = new SplitLayout(menu, viewContainer);

        HorizontalLayout layout = new HorizontalLayout(menu, viewContainer);
        layout.setSizeFull();
        setContent(layout);
        Navigator navigator = new Navigator(this, viewContainer);
        navigator.addView("", PlaygroundView.class);
        navigator.addView(getViewName(PlaygroundView.class), PlaygroundView.class);
        navigator.addView(getViewName(SettingsView.class), SettingsView.class);
        navigator.addView(getViewName(HelpView.class), HelpView.class);
    }

    public String getRummikubDescription() {
        return rummikubDescription;
    }

    public void setRummikubDescription(String rummikubDescription) {
        this.rummikubDescription = rummikubDescription;
    }

    private String getViewName(Class<?> classz) {
        return classz.getSimpleName().toLowerCase().replace("view", "");
    }
    
}

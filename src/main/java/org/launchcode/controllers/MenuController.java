package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("menu")
public class MenuController {
    @Autowired
    private MenuDao menuDao;
    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value="")
    public String index(Model model){
        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menus");
        return "menu/index";
    }

    @RequestMapping(value="add")
    public String add(Model model){
        model.addAttribute(new Menu());
        model.addAttribute("title", "Add Menus");
        return "menu/add";
    }

    @RequestMapping(value="add", method = RequestMethod.POST)
    public String add(Model model, @ModelAttribute @Valid Menu menu, Errors errors){
        if(errors.hasErrors()){
            model.addAttribute("title", "Add Menus");
            return "menu/add";
        } else{
            menuDao.save(menu);

            return "redirect:view/" + menu.getId();
        }
    }

    @RequestMapping(value="view/{id}", method = RequestMethod.GET)
                //OH - this gets the id from the URL; the id is already there because in index.html,
                //the link goes to menu/view/{some_id} already! So just need to get that id.
    public String viewMenu(Model model, @PathVariable int id){
        model.addAttribute("menu", menuDao.findOne(id));
        model.addAttribute("title", menuDao.findOne(id).getName());

        return "menu/view"; //This will go to the right id

    }

    @RequestMapping(value="add-item/{id}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int id){
        model.addAttribute("form", new AddMenuItemForm(menuDao.findOne(id), cheeseDao.findAll()));
        model.addAttribute("title", "Add Items to: " + menuDao.findOne(id).getName());

        return "menu/add-item";
    }

    @RequestMapping(value = "add-item/{id}", method = RequestMethod.POST)
    public String addItem(@ModelAttribute  @Valid AddMenuItemForm addMenuItemForm, Errors errors,
                          @RequestParam int menuId, @RequestParam int cheeseId, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Items to: " + menuDao.findOne(menuId).getName());
            return "menu/add-item";
        } else{
            Menu theMenu = menuDao.findOne(menuId);
            theMenu.addItem(cheeseDao.findOne(cheeseId));
            menuDao.save(theMenu);
            return "redirect:/menu/view/"+theMenu.getId();
        }

    }
}

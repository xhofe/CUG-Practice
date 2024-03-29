# Generated by Selenium IDE
import pytest
import time
import json
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.action_chains import ActionChains
from selenium.webdriver.support import expected_conditions
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.desired_capabilities import DesiredCapabilities

class Test():
  def setup_method(self, method):
    self.driver = webdriver.Chrome()
    self.vars = {}
  
  def teardown_method(self, method):
    self.driver.quit()
  
  def test_(self):
    # Test name: 裁剪图片
    # Step # | name | target | value
    # 1 | open | / | 
    self.driver.get("http://app.xunjietupian.com/")
    # 2 | setWindowSize | 1552x840 | 
    self.driver.set_window_size(1552, 840)
    # 3 | click | css=.raw:nth-child(1) > a:nth-child(1) img | 
    self.driver.find_element(By.CSS_SELECTOR, ".raw:nth-child(1) > a:nth-child(1) img").click()
    # 4 | click | css=.btn-first-load | 
    # self.driver.find_element(By.CSS_SELECTOR, ".btn-first-load").click()
    # 5 | type | css=.tui-image-editor-load-btn | E:/code/PycharmProjects/Test2/img/test.jpg
    upload=self.driver.find_element(By.CSS_SELECTOR, ".tui-image-editor-load-btn")
    time.sleep(1)
    upload.send_keys("E:/code/PycharmProjects/Test2/img/test.jpg")
    time.sleep(1)
    # 6 | mouseDownAt | css=.upper-canvas | 493.3999938964844,281.3999938964844
    element = self.driver.find_element(By.CSS_SELECTOR, ".upper-canvas")
    actions = ActionChains(self.driver)
    actions.move_to_element(element).move_by_offset(-100,-100).click_and_hold().move_by_offset(10,10).perform()
    # 7 | mouseMoveAt | css=.upper-canvas | 493.3999938964844,281.3999938964844
    # element = self.driver.find_element(By.CSS_SELECTOR, ".upper-canvas")
    # actions = ActionChains(self.driver)
    # actions.move_to_element(element).perform()
    # # 8 | mouseUpAt | css=.upper-canvas | 493.3999938964844,281.3999938964844
    # element = self.driver.find_element(By.CSS_SELECTOR, ".upper-canvas")
    actions = ActionChains(self.driver)
    actions.move_to_element(element).release().perform()
    # 9 | click | css=.upper-canvas | 
    # self.driver.find_element(By.CSS_SELECTOR, ".upper-canvas").click()
    # 10 | click | css=.btn-crop-apply | 
    self.driver.find_element(By.CSS_SELECTOR, ".btn-crop-apply").click()
    self.driver.find_element(By.CSS_SELECTOR,".btn-crop-reset").click()
    element = self.driver.find_element(By.CSS_SELECTOR, ".upper-canvas")
    actions = ActionChains(self.driver)
    actions.move_to_element(element).move_by_offset(-100,-100).click_and_hold().move_by_offset(10,10).perform()
    # 7 | mouseMoveAt | css=.upper-canvas | 493.3999938964844,281.3999938964844
    # element = self.driver.find_element(By.CSS_SELECTOR, ".upper-canvas")
    # actions = ActionChains(self.driver)
    # actions.move_to_element(element).perform()
    # # 8 | mouseUpAt | css=.upper-canvas | 493.3999938964844,281.3999938964844
    # element = self.driver.find_element(By.CSS_SELECTOR, ".upper-canvas")
    actions = ActionChains(self.driver)
    actions.move_to_element(element).release().perform()
    # 9 | click | css=.upper-canvas |
    # self.driver.find_element(By.CSS_SELECTOR, ".upper-canvas").click()
    # 10 | click | css=.btn-crop-apply |
    self.driver.find_element(By.CSS_SELECTOR, ".btn-crop-apply").click()
    # 11 | click | css=.btn-down-load | 
    self.driver.find_element(By.CSS_SELECTOR, ".btn-down-load").click()
    time.sleep(2)
    self.driver.close()
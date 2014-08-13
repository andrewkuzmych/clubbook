//
//  WebViewController.h
//  Clubbook
//
//  Created by Andrew on 8/4/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface WebViewController : UIViewController
@property (weak, nonatomic) IBOutlet UIWebView *webBrowser;
@property (weak, nonatomic) NSString *web;

@end

//
//  OBAlert.m
//  OBAlert
//
//  Extended by Onur Baykal on 15/09/13
//
//  Based on ALFullScreenAlert:
//  Created by Andrea Mario Lufino on 02/07/13.
//  Copyright (c) 2013 Andrea Mario Lufino. All rights reserved.
//

#import "OBAlert.h"

#define kNumberOfLines 99
#define kMessageMaxHeight 380
#define kMessageWidth 200
#define kTitleMaxHeight 300
#define kTitleWidth 200
#define kX 55
#define kCenteredX 320/2
#define kBtnWidth (kMessageWidth)/2
#define kBtnY message.frame.origin.y + message.frame.size.height + 30
#define kBtnHeight 24

#define BTN_FONT [UIFont fontWithName:@"HelveticaNeue-Bold" size:12]
#define BTN_CENTERED CGRectMake(kCenteredX - kBtnWidth/2, kBtnY, kBtnWidth, kBtnHeight)


@implementation OBAlert

#pragma mark - Init methods

//Init ALFSAlert object using initInViewController:viewController 
- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [NSException raise:@"init exception" format:@"Use ONLY initInViewController:viewController to init OBAlert object"];
    }
    return self;
}

- (id)init {
    self = [super init];
    if (self) {
        [NSException raise:@"init exception" format:@"Use ONLY initInViewController:viewController to init OBAlert object"];
    }
    return self;
}

//Get the instance of the calling view controller and create the overlay
- (id)initInViewController:(UIViewController *)viewController {
    self = [super initWithFrame:viewController.view.bounds];
    if (self) {
        parentViewController = viewController;
        overlay = [[UIView alloc] initWithFrame:self.frame];
        overlay.userInteractionEnabled = NO;
        
        [overlay setBackgroundColor:[UIColor colorWithRed:0 green:0 blue:0 alpha:1]];
        subviews = [[NSMutableArray alloc] init];
        isShown = NO;
    }
    return self;
}

#pragma mark - Build user interface

//Create the message label
- (void)showAlertWithText:(NSString *)alertText titleText:(NSString *)titleText 
{
    
    isShown = YES;

    UIFont *font = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:13.0];
    message = [[UILabel alloc] init];
    
    UILabel *gettingSizeLabel = [[UILabel alloc] init];
    gettingSizeLabel.font = font;
    gettingSizeLabel.text = message.text;
    gettingSizeLabel.numberOfLines = message.numberOfLines;
    gettingSizeLabel.lineBreakMode = message.lineBreakMode;
    CGSize maximumLabelSize = CGSizeMake(kMessageWidth, kMessageMaxHeight);
    
    CGSize textSize = [gettingSizeLabel sizeThatFits:maximumLabelSize];
    
    CGFloat y = kMessageMaxHeight/2 - textSize.height/2;
    [message setFrame:CGRectMake(kX, y, kMessageWidth, 170)];
    [message setFont:font];
    [message setBackgroundColor:[UIColor clearColor]];  
    [message setTextColor:[UIColor whiteColor]];
    [message setText:alertText];
    [message setTextAlignment:NSTextAlignmentCenter];
    [message setNumberOfLines:kNumberOfLines];
    
    title = [[UILabel alloc] init];

    gettingSizeLabel.font = font;
    gettingSizeLabel.text = titleText;
    gettingSizeLabel.numberOfLines = title.numberOfLines;
    gettingSizeLabel.lineBreakMode = title.lineBreakMode;
    maximumLabelSize = CGSizeMake(kTitleWidth, kTitleMaxHeight);
    
    CGSize titleSize = [gettingSizeLabel sizeThatFits:maximumLabelSize];
    
    y = kTitleMaxHeight/2 - titleSize.height/2;
    [title setFrame:CGRectMake(kX, y, kTitleWidth, 20)];
    UIFont * titleFont = [UIFont fontWithName:NSLocalizedString(@"fontRegular", nil) size:20.0];
    [title setFont:titleFont];
    [title setBackgroundColor:[UIColor clearColor]];
    [title setTextColor:[UIColor whiteColor]];
    [title setText:titleText];
    [title setTextAlignment:NSTextAlignmentCenter];
    [title setNumberOfLines:1];
    
    /*
    button = [UIButton buttonWithType:UIButtonTypeCustom];
    [button setBackgroundColor:[UIColor redColor]];
    [button setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [button setTitleColor:[UIColor darkGrayColor] forState:UIControlStateHighlighted];
    [button.layer setCornerRadius:2];
    [[button titleLabel] setFont:BTN_FONT];
    [button setTitle:buttonText forState:UIControlStateNormal];
    
    if (block != nil)
    {
        [button setAction:kUIButtonBlockTouchUpInside withBlock:block];
    }
    
    button.frame = BTN_CENTERED;
    */
    
    //remove all gesture recognizers
    gestureRecodnizers = parentViewController.view.gestureRecognizers;
    while (parentViewController.view.gestureRecognizers.count) {
        [parentViewController.view  removeGestureRecognizer:[parentViewController.view .gestureRecognizers objectAtIndex:0]];
    }
    
    [parentViewController.view addSubview:overlay];
    [parentViewController.view addSubview:message];
    [parentViewController.view addSubview:title];
    //[parentViewController.view addSubview:button];
    
    [parentViewController.navigationController setNavigationBarHidden:YES];
    [subviews addObject:overlay];
    [subviews addObject:message];
    [subviews addObject:title];
    //[subviews addObject:button];
}

- (void)removeAlert {
    for (UIView *view in subviews) {
        [view removeFromSuperview];
    }
    
    //add back all gesture recognizers
    //gestureRecodnizers = parentViewController.view.gestureRecognizers;
    for (int i=0; i < gestureRecodnizers.count; i++) {
        [parentViewController.view addGestureRecognizer:[gestureRecodnizers objectAtIndex:i]];
    }
    
    [parentViewController.navigationController setNavigationBarHidden:NO];
    isShown = NO;
}

@end


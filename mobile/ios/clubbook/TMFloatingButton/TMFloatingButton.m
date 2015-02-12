//
//  FloatingButton.m
//  Hagtap
//
//  Created by Admin on 12/9/14.
//  Copyright (c) 2014 Ihor. All rights reserved.
//

#import "TMFloatingButton.h"
@implementation TMFloatingButtonState

- (id)initWithView:(UIView *)view andBackgroundColor:(UIColor *)bgColor {
    self = [super init];
    if (self)
    {
        _view = view;
        _bgColor = bgColor;
    }
    
    return self;
}
- (id)initWithView:(UIView *)view andBackgroundColor:(UIColor *)bgColor forButton:(TMFloatingButton *)button {
    self = [super init];
    if (self)
    {
        _view = view;
        [_view setFrame:button.bounds];
        _bgColor = bgColor;
    }
    return self;
}
- (id)initWithText:(NSString *)text andBackgroundColor:(UIColor *)bgColor forButton:(TMFloatingButton *)button {
    self = [self initWithIcon:nil andText:text andBackgroundColor:bgColor forButton:button];
    return self;
}
- (id)initWithIcon:(UIImage *)icon andBackgroundColor:(UIColor *)bgColor forButton:(TMFloatingButton *)button
{
    self = [self initWithIcon:icon andText:nil andBackgroundColor:bgColor forButton:button];
    return self;
}
- (id)initWithIcon:(UIImage *)icon andText:(NSString *)text andBackgroundColor:(UIColor *)bgColor forButton:(TMFloatingButton *)button {
    CGFloat margin = 15;
    UIView *stateView = [[UIView alloc]initWithFrame:button.bounds];
    if(text != nil && icon != nil)
    {
        UIImageView *stateIcon = [[UIImageView alloc]initWithFrame:CGRectMake(margin, margin, button.frame.size.width - 2 * margin, button.frame.size.height - 2 * margin)];
        stateIcon.image = icon;
        
        CGRect imageFrame = stateIcon.frame;
        imageFrame.origin.y = 9;
        stateIcon.frame = imageFrame;
        
        //TODO: add text attributes
        UILabel *stateLabel;
        stateLabel = [[UILabel alloc]initWithFrame:CGRectMake(stateIcon.frame.origin.x, CGRectGetMaxY(stateIcon.frame) - 1.0, stateIcon.frame.size.width, 10)];
        stateLabel.text = text;
        stateLabel.font = [UIFont systemFontOfSize:10];
        stateLabel.minimumScaleFactor = 0.5;
        stateLabel.textAlignment = NSTextAlignmentCenter;
        stateLabel.textColor = [UIColor whiteColor];
        
        [stateView addSubview:stateIcon];
        [stateView addSubview:stateLabel];
    }
    else if(text != nil)
    {
        //TODO: add text attributes
        margin = 3.0;
        UILabel *stateLabel;
        stateLabel = [[UILabel alloc]initWithFrame:CGRectMake(margin,(stateView.frame.size.height - 40) / 2.0, stateView.frame.size.width - 2.0 * margin, 40)];
        stateLabel.text = text;
        stateLabel.font = [UIFont systemFontOfSize:12];
        stateLabel.minimumScaleFactor = 0.5;
        stateLabel.textAlignment = NSTextAlignmentCenter;
        stateLabel.textColor = [UIColor whiteColor];
        stateLabel.numberOfLines = 0;
        [stateView addSubview:stateLabel];
    }
    else if(icon != nil)
    {
        margin = 13;
        UIImageView *iconView = [[UIImageView alloc]initWithFrame:CGRectMake(margin, margin, button.frame.size.width - 2 * margin, button.frame.size.height - 2 * margin)];
        iconView.image = icon;
        [stateView addSubview:iconView];
    }
    stateView.userInteractionEnabled = NO;
    self = [self initWithView:stateView andBackgroundColor:bgColor forButton:button];
    return self;
}
@end


@interface TMFloatingButton ()
{
    UIActivityIndicatorView *activityIndicator;
    TMFloatingButtonState *curState;
    NSMutableDictionary *buttonStates;
    //hide & show
    CGRect showFrame;
    CGRect hideFrame;
}

@end

@implementation TMFloatingButton


- (id)initWithWidth:(CGFloat)width withMargin:(CGFloat)margin andPosition:(FloatingButtonPosition)postion andHideDirection:(FloatingButtonHideDirection)hideDirection andSuperView:(UIView *)superView {
    self = [super init];//[RoundFloatingButton buttonWithType:UIButtonTypeCustom];
    
    if (self)
    {
        //set frame that depends from 'postion'
        CGRect frameForButton;
        switch (postion) {
            case FloatingButtonPositionTopLeft:
                frameForButton = CGRectMake(margin, margin, width, width);
                break;
                
            case FloatingButtonPositionTopRight:
                frameForButton = CGRectMake(superView.frame.size.width - width - margin, margin, width, width);
                break;
                
            case FloatingButtonPositionBottomLeft:
                frameForButton = CGRectMake(margin, superView.frame.size.height - width - margin, width, width);
                break;
                
            case FloatingButtonPositionBottomRight:
            {
                float x = superView.frame.size.width - width - margin;
                float y = superView.frame.size.height - width - margin - 110;
                frameForButton = CGRectMake(x, y, width, width);
                break;
            }
            default:
                break;
        }
        self.frame = frameForButton;
        
        //rounded
        _isRounded = NO;
        [self setIsRounded:YES];
        _showShadow = NO;
        [self setShowShadow:YES];
        
        //calculate hided frame
        hideFrame = self.frame;
        showFrame = self.frame;
        switch (hideDirection) {
            case FloatingButtonHideDirectionLeft:
                hideFrame.origin.x = -showFrame.size.width;
                break;
                
            case FloatingButtonHideDirectionRight:
                
                hideFrame.origin.x = superView.frame.size.width;
                break;
                
            case FloatingButtonHideDirectionUp:
                hideFrame.origin.y = -showFrame.size.height;
                break;
                
            case FloatingButtonHideDirectionDown:
                hideFrame.origin.y = superView.frame.size.height;
                break;
                
            default:
                break;
        }
        //init activity indicator
        activityIndicator  = [[UIActivityIndicatorView alloc]initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
        [self addSubview:activityIndicator];
        activityIndicator.center = CGPointMake(self.frame.size.width / 2.0, self.frame.size.height / 2.0);
        //states
        curState = nil;
        if (buttonStates == nil)
        {
            buttonStates = [[NSMutableDictionary alloc]init];
        }
        
        [self setBackgroundImage:[UIImage imageNamed:@"selectorImage"] forState:UIControlStateHighlighted];
        
        [self addAsSubviewToView:superView];
    }
    return self;
}
- (void)setIsRounded:(BOOL)isRounded {
    if (_isRounded != isRounded)
    {
        if (isRounded)
        {
            self.layer.cornerRadius = self.bounds.size.width / 2.0;
        }
        else {
            self.layer.cornerRadius = 0.0;
        }
        _isRounded = isRounded;
    }
}
- (void)setShowShadow:(BOOL)showShadow {
    if (_showShadow != showShadow)
    {
        if (showShadow)
        {
            [self.layer setShadowColor:[UIColor blackColor].CGColor];
            [self.layer setShadowOpacity:0.7];
            [self.layer setShadowRadius:3.0];
            [self.layer setShadowOffset:CGSizeMake(1.0, 1.0)];
        }
        else {
            [self.layer setShadowColor:[UIColor clearColor].CGColor];
            [self.layer setShadowOpacity:0.0];
            [self.layer setShadowRadius:0.0];
            [self.layer setShadowOffset:CGSizeMake(0.0, 0.0)];
        }
        // self.clipsToBounds = YES;
        _showShadow = showShadow;
    }
}
- (void)hideAnimated:(BOOL)animated {
    if (!self.superview)
    {
        return;
    }
    
    
    if (animated)
    {
        [UIView animateWithDuration:0.5 animations: ^(void) {
            self.frame = hideFrame;
        } completion: ^(BOOL finished)
         {}];
    }
    else {
        self.frame = hideFrame;
    }
}
- (void)showAnimated:(BOOL)animated {
    if (!self.superview)
    {
        return;
    }
    
    if (animated)
    {
        [UIView animateWithDuration:0.5 animations: ^(void) {
            self.frame = showFrame;
        } completion: ^(BOOL finished)
         {}];
    }
    else {
        self.frame = showFrame;
    }
}
- (void)addAsSubviewToView:(UIView *)superView {
    if (superView == nil)
    {
        NSLog(@"WARNING!: superview is nil!");
    }
    [superView addSubview:self];
    [superView bringSubviewToFront:self];
}
- (void)animateActivityIndicatorStart:(BOOL)animate {
    if (animate)
    {
        [curState.view removeFromSuperview];
        [activityIndicator startAnimating];
    }
    else {
        [activityIndicator stopAnimating];
        [self addSubview:curState.view];
    }
}
- (void)addState:(TMFloatingButtonState *)state forName:(NSString *)stateName {
    if (state && stateName)
    {
        [buttonStates setObject:state forKey:stateName];
    }
}
- (void)addAndApplyState:(TMFloatingButtonState *)state forName:(NSString *)stateName
{
    [self addState:state forName:stateName];
    [self setButtonState:stateName];
}
- (void)setButtonState:(NSString *)name {
    TMFloatingButtonState *stateToSet = [buttonStates objectForKey:name];
    if (![stateToSet isEqual:curState] && stateToSet != nil)
    {
        [curState.view removeFromSuperview];
        curState = stateToSet;
        if (curState.view)
        {
            [self addSubview:curState.view];
        }
        if (curState.bgColor)
        {
            self.backgroundColor = curState.bgColor;
        }
    }
}
#pragma mark - styles
+ (void)addFavoritesStyleToButton:(TMFloatingButton *)button {
    //NOT SAVED
    TMFloatingButtonState *notSaved = [[TMFloatingButtonState alloc] initWithIcon:[UIImage imageNamed:@"white-star"] andBackgroundColor:[UIColor colorWithRed:0.662 green:0.088 blue:0.719 alpha:0.800] forButton:button];
    
    //SAVED
    TMFloatingButtonState *saved = [[TMFloatingButtonState alloc] initWithIcon:[UIImage imageNamed:@"checkmark-white"] andText:NSLocalizedString(@"Saved", @"Saved") andBackgroundColor:[UIColor colorWithRed:0.101 green:0.510 blue:0.133 alpha:0.800] forButton:button];
    
    [button addState:notSaved forName:@"notSaved"];
    [button addState:saved forName:@"saved"];
}
+ (void)addModeEditStyleToButton:(TMFloatingButton *)button {
    TMFloatingButtonState *statickStyle = [[TMFloatingButtonState alloc] initWithIcon:[UIImage imageNamed:@"white-mode-edit"] andBackgroundColor:[UIColor colorWithRed:0.792 green:0.169 blue:0.149 alpha:1.000] forButton:button];
    [button addState:statickStyle forName:@"statickStyle"];
    
    [button setButtonState:@"statickStyle"];
}
+ (void)addMessageStyleToButton:(TMFloatingButton *)button {
    TMFloatingButtonState *statickStyle = [[TMFloatingButtonState alloc] initWithIcon:[UIImage imageNamed:@"message_grey"] andBackgroundColor:[UIColor whiteColor] forButton:button];
    
    
    [button addState:statickStyle forName:@"staticStyle"];
    
    [button setButtonState:@"staticStyle"];
}
@end
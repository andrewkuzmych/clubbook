//
//  CbButton.h
//  Clubbook
//
//  Created by Andrew on 7/4/14.
//  Copyright (c) 2014 clubbook. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface CbButton : UIButton
{
    @private
    NSMutableDictionary *backgroundStates;
    UIColor * highlightedColor;
    UIColor * normalColor;
    
    @public
}

@property(nonatomic) BOOL isCheckin;

- (void) setMainState:(NSString*) text;
- (void) setSecondState:(NSString*) text;
//- (void) setBackgroundColor:(UIColor *) _backgroundColor forState:(UIControlState) _state;
- (UIColor*) backgroundColorForState:(UIControlState) _state;

@end